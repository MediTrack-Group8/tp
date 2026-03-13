# Product Requirements Document (PRD)

## 1. Product Overview

MediTrack is a standalone **Java desktop GUI application** designed to help military field medics manage medical logistics and personnel readiness during deployments and training exercises.

The system enables users to track medical supply inventories, monitor supply expiration dates, and manage the medical readiness status of personnel within a unit. By digitizing these administrative tasks, MediTrack reduces manual errors and improves operational efficiency in high-pressure environments.

---

## 2. Problem Statement

Military field medics often rely on manual tracking methods such as paper logs or spreadsheets to manage medical supplies and personnel readiness. These methods are inefficient and prone to errors.

Several operational problems arise from these manual processes:

- Expiring medical supplies may go unnoticed.
- Personnel medical readiness is difficult to track quickly.
- Determining deployable manpower requires manually checking individual records.
- Manual data management increases the risk of mistakes during high-stress situations.

MediTrack addresses these problems by providing a centralized digital system that allows users to efficiently track medical supplies and personnel readiness.

---

## 3. Target Users / Stakeholders

### Primary Users

**Field Medics**

- Responsible for maintaining medical supply inventories.
- Need quick access to supply status and personnel readiness during operations.

### Secondary Users

**Medical Officers / Platoon Commanders**

- Need a quick overview of personnel medical readiness.
- Use the system to determine deployable manpower.

**Logistics Officers**

- Need visibility of current medical supply levels.
- Use the information to plan future resupply operations.

---

## 4. User Stories

**Inventory Management**

As a field medic, I want to add new medical supplies with quantity and expiry dates so that the inventory tracker remains accurate and up to date.

---

**Expiry Monitoring**

As a field medic, I want the system to display a list of supplies that will expire within the next 30 days so that I can replace them before they become unusable during operations.

---

**Personnel Status Tracking**

As a medical officer, I want to update a soldier’s medical status (e.g., Fit, Light Duties, Unfit) so that I know who is medically deployable.

---

**Operational Inspection**

As a platoon commander, I want to view a list of personnel who are marked as “Fit” so that I can quickly determine the available manpower for deployment.

---

**Duty Rostering**

As a field medic, I want the system to generate a randomized duty roster from personnel marked as “Fit” so that I can reduce the time spent manually assigning duties during exercises.

---

**Supply Visibility**

As a logistics officer, I want to view current medical supply inventory levels so that I can plan resupply operations effectively.

---

## 5. Functional Requirements

### Inventory Management

The system shall allow users to:

- Add new medical supply items.
- Update supply information such as quantity and expiry date.
- Delete supply records when items are consumed or expired.
- View a list of all medical supplies.

Each **Supply** item includes:

- Name
- Quantity
- Expiry date

---

### Personnel Management

The system shall allow users to:

- Add new personnel records.
- Update personnel medical status.
- Remove personnel records.
- View the list of personnel.

Each **Personnel** record includes:

- Name
- Medical readiness status

Personnel status values include:

- **FIT**
- **LIGHT_DUTIES**
- **UNFIT**

---

### Expiration Monitoring

The system shall:

- Retrieve the system date from the user's computer.
- Calculate the remaining time before supply expiration.
- Identify supplies that will expire within 30 days.

---

### Personnel Filtering

The system shall:

- Filter personnel records based on readiness status.
- Display personnel marked as **FIT** when required.

---

### Duty Roster Generation

The system shall:

- Retrieve the list of personnel marked as **FIT**.
- Randomly assign these personnel to duty slots.

---

## 6. Non-Functional Requirements

### Data Persistence

All application data must be stored in a local JSON file and automatically loaded when the application starts, ensuring the system works without internet connectivity.

---


### Performance

The system should process user commands and update the user interface within 1 second when managing up to 200 supply or personnel records.

---

### Reliability

The system should detect invalid inputs and display appropriate error messages without crashing the application.

---

### Data Integrity

The system should prevent invalid data entries such as:

- Negative supply quantities
- Invalid expiration dates
- Missing required information

---
