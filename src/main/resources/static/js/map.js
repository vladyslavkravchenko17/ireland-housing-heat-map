document.addEventListener("DOMContentLoaded", () => {
    const map = L.map('map').setView([53.5, -8.0], 7);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    let geoLayer;
    const markersLayer = L.layerGroup().addTo(map);
    const priceMap = {};

    function updatePriceMap(prices) {
        Object.keys(priceMap).forEach(key => delete priceMap[key]);
        prices.forEach(p => {
            priceMap[p.county.toLowerCase()] = p.medianPrice;
        });
    }

    function getColorByAffordability(price, filters) {
        // Average monthly income in Ireland (€3,683)
        // Comfortable housing budget: 35% for rent, 45% for mortgage
        const avgMonthlyIncome = 3683;
        const comfortPercent = filters.transactionType === 'rent' ? 0.35 : 0.45;
        const comfortableMonthlyBudget = avgMonthlyIncome * comfortPercent;

        // For purchase, estimate mortgage payment assuming 4.5% annual interest
        const monthlyCost = filters.transactionType === 'rent'
            ? price
            : (price * 0.045) / 12;

        const ratio = comfortableMonthlyBudget / monthlyCost;
        const clamped = Math.max(0.3, Math.min(1.5, ratio));
        const percent = (clamped - 0.3) / (1.5 - 0.3);

        return affordabilityGradient(percent);
    }

    function affordabilityGradient(percent) {
        const colors = [
            [178, 34, 34],
            [255, 69, 0],
            [255, 165, 0],
            [255, 255, 0],
            [144, 238, 144],
            [0, 128, 0]
        ];

        const idx = percent * (colors.length - 1);
        const low = Math.floor(idx);
        const high = Math.ceil(idx);
        const t = idx - low;

        const lerp = (a, b, t) => Math.round(a + (b - a) * t);
        const color = colors[low].map((c, i) => lerp(c, colors[high][i], t));

        return `rgb(${color[0]}, ${color[1]}, ${color[2]})`;
    }

    function getPolygonCentroid(coords) {
        let area = 0, x = 0, y = 0;
        const points = coords[0];
        for (let i = 0, j = points.length - 1; i < points.length; j = i++) {
            const xi = points[i][0], yi = points[i][1];
            const xj = points[j][0], yj = points[j][1];
            const f = xi * yj - xj * yi;
            area += f;
            x += (xi + xj) * f;
            y += (yi + yj) * f;
        }
        area *= 0.5;
        x /= (6 * area);
        y /= (6 * area);
        return [y, x];
    }

    function getCentroidFromGeometry(geometry) {
        if (geometry.type === "Polygon") {
            return getPolygonCentroid(geometry.coordinates);
        } else if (geometry.type === "MultiPolygon") {
            let largest = geometry.coordinates[0];
            let maxLen = largest[0].length;
            geometry.coordinates.forEach(polygon => {
                if (polygon[0].length > maxLen) {
                    largest = polygon;
                    maxLen = polygon[0].length;
                }
            });
            return getPolygonCentroid(largest);
        }
        return null;
    }

    function drawMap(geoData) {
        if (geoLayer) {
            map.removeLayer(geoLayer);
        }
        markersLayer.clearLayers();

        geoLayer = L.geoJSON(geoData, {
            style: feature => {
                const price = priceMap[feature.properties.name.toLowerCase()];
                return {
                    fillColor: price !== undefined
                        ? getColorByAffordability(price, getSelectedFilters())
                        : '#cccccc',
                    weight: 2,
                    opacity: 1,
                    color: 'white',
                    dashArray: '3',
                    fillOpacity: 0.7
                };
            },
            onEachFeature: (feature, layer) => {
                const name = feature.properties.name;
                const price = priceMap[name.toLowerCase()];
                layer.bindPopup(`<b>${name}</b><br>Median Price: €${price !== undefined ? price.toLocaleString() : 'N/A'}`);

                if (price !== undefined) {
                    const center = getCentroidFromGeometry(feature.geometry);
                    if (center) {
                        const marker = L.marker(center, {
                            icon: L.divIcon({
                                className: 'label',
                                html: `€${Math.round(price / 1000)}k`,
                                iconSize: [40, 20],
                                iconAnchor: [20, 10]
                            })
                        });
                        markersLayer.addLayer(marker);
                    }
                }
            }
        }).addTo(map);
    }

    const rentBtn = document.querySelector('#transactionType button[data-value="rent"]');
    const anyPropertyBtn = document.querySelector('#propertyTypes button[data-value=""]');
    rentBtn.classList.add('selected');
    anyPropertyBtn.classList.add('selected');

    document.querySelectorAll('#transactionType button.toggle').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('#transactionType button.toggle').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
        });
    });

    const propertyButtons = document.querySelectorAll('#propertyTypes button');
    propertyButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            const isAny = btn.dataset.value === "";

            if (isAny) {
                propertyButtons.forEach(b => b.classList.remove('selected'));
                btn.classList.add('selected');
            } else {
                anyPropertyBtn.classList.remove('selected');
                btn.classList.toggle('selected');

                const anySelected = Array.from(propertyButtons).some(b => b.dataset.value !== "" && b.classList.contains('selected'));
                if (!anySelected) {
                    anyPropertyBtn.classList.add('selected');
                }
            }
        });
    });

    fetch('/data/ireland-counties.geojson')
        .then(response => response.json())
        .then(geoData => {
            window._geoData = geoData;
            fetchAndRenderMapData();
        });

    function getSelectedFilters() {
        const transactionType = document.querySelector('#transactionType .selected')?.dataset.value;
        const propertyTypes = Array.from(document.querySelectorAll('#propertyTypes .selected'))
            .map(btn => btn.dataset.value)
            .filter(v => v !== "");

        const sizeFrom = document.getElementById('sizeFrom').value;
        const sizeTo = document.getElementById('sizeTo').value;
        const bedsFrom = document.getElementById('bedsFrom').value;
        const bedsTo = document.getElementById('bedsTo').value;

        return {
            transactionType,
            propertyTypes,
            sizeFrom,
            sizeTo,
            bedsFrom,
            bedsTo
        };
    }

    function updateURLWithFilters(filters) {
        const params = new URLSearchParams();

        if (filters.transactionType) {
            params.set('transactionType', filters.transactionType);
        }
        if (filters.propertyTypes.length > 0) {
            filters.propertyTypes.forEach(pt => params.append('propertyTypes', pt));
        }
        if (filters.sizeFrom) params.set('sizeFrom', filters.sizeFrom);
        if (filters.sizeTo) params.set('sizeTo', filters.sizeTo);
        if (filters.bedsFrom) params.set('bedsFrom', filters.bedsFrom);
        if (filters.bedsTo) params.set('bedsTo', filters.bedsTo);

        const newUrl = `/?${params.toString()}`;
        history.pushState(null, '', newUrl);
    }

    async function fetchAndRenderMapData() {
        const filters = getSelectedFilters();
        updateURLWithFilters(filters);

        const params = new URLSearchParams();
        if (filters.transactionType) params.set("transactionType", filters.transactionType);
        if (filters.propertyTypes.length > 0) filters.propertyTypes.forEach(pt => params.append("propertyTypes", pt));
        if (filters.sizeFrom) params.set("sizeFrom", filters.sizeFrom);
        if (filters.sizeTo) params.set("sizeTo", filters.sizeTo);
        if (filters.bedsFrom) params.set("bedsFrom", filters.bedsFrom);
        if (filters.bedsTo) params.set("bedsTo", filters.bedsTo);

        const res = await fetch(`/api/prices?${params.toString()}`);
        const data = await res.json();
        updatePriceMap(data);
        drawMap(window._geoData);
    }

    document.getElementById('showResultsBtn').addEventListener('click', () => {
        fetchAndRenderMapData();
    });
});
