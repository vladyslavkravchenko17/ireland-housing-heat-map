# 🏡 Ireland Housing HeatMap — Interactive Property Price Visualization in Ireland

**Live demo with synthetic data · Built with Spring Boot + PostgreSQL + Geo APIs**

---

## 🎯 About the Project

Ireland Housing HeatMap is a personal project aimed at showcasing my backend and full-stack development skills in the context of the Irish property market. It was developed specifically to demonstrate interest in **Distilled**, the company behind [daft.ie](https://daft.ie).

While it **does not use real listing data** (in full respect of [daft.ie’s data usage policy](https://support.daft.ie/hc/en-ie/sections/360000553658-Policies-Terms-and-Conditions)), the app simulates real-world functionality by using **synthetic listings** and connecting to **live geolocation APIs** for reverse geocoding and county detection.

---

## 💡 What It Does

- 📍 Detects Irish **county** from a given **address**, using coordinates and a GeoJSON map  
- 🧮 Calculates **median property prices per county**, based on filtered synthetic listings  
- 🗺️ Displays results on an **interactive map** with **color-coded affordability levels**  
- 🔎 Supports filtering by rent/sale, property type, area (sqm), and number of bedrooms  
- 💻 Built with clean architecture, tested services, and a responsive interface  

---

## 📌 Why I Built It

> I developed this project to express my technical enthusiasm and interest in working with the team at Distilled.

It’s designed to demonstrate my approach to:

- Data-driven backend logic  
- Real-time visual feedback
- Working with geolocation and filtering systems  
- Code quality and attention to design

---

## 🛠️ Tech Stack

| Layer       | Technologies                        |
|-------------|-------------------------------------|
| Backend     | Java 17, Spring Boot, JPA, PostgreSQL |
| Frontend    | HTML, CSS, JavaScript, Thymeleaf    |
| Geolocation | Nominatim API, GeometryFactory (JTS), GeoJSON |
| Testing     | JUnit 5, Mockito                    |
| Build       | Maven                               |

---

## 💻 Live Demo

🚀 **Try the live version**:  
🔗 [Live Demo / https://ireland-housing-heat-map-5a86dedc00a0.herokuapp.com/?transactionType=rent]

---

## 📸 Screenshots

![Main page screenshot](https://github.com/vladyslavkravchenko17/ireland-housing-heat-map/blob/1ccc85a6b3ee9461f32e2f47e5c7590b3dc8d160/screenshot.png)

---

## 📦 Features Recap

✅ Address-based geolocation  
✅ GeoJSON-based county detection  
✅ Filtering system for listings  
✅ Median price aggregation  
✅ Color-coded affordability  
✅ Responsive UI (JS + Thymeleaf)  
✅ Fully tested backend services  

---

## 👨‍💻 About the Developer

**Vladyslav Kravchenko**  
Java Backend Developer · 3+ years experience · Strong background in Computer Engineering principles 

🇺🇦 From Ukraine | 🇮🇪 Prepared to relocate to Ireland under Temporary Protection  
🎯 Seeking Java Developer roles - especially at **Distilled**

---

## 💬 Looking for a Java Developer?

If you’re looking for a developer who:

- Thinks in systems  
- Writes clean, testable Java code  
- Understands geolocation and data  
- Takes initiative and ships things  

then I’d love to connect - [LinkedIn](https://www.linkedin.com/in/vladyslavkravchenko/)!

---

## Final Notes

- The `.geojson` file is based on [Plotly’s Ireland dataset](https://github.com/plotly)  
- The app uses synthetic listings only — not real daft.ie data
