# MediTrack Internal API Documentation

## Introduction
MediTrack is a standalone Java desktop point-and-click GUI application engineered to streamline logistics and 
personnel readiness for military field medics. All user interactions are performed via clickable GUI controls 
(buttons, dropdowns, modals). To ensure the system remains maintainable, scalable, and easy to test, 
it is built upon a modular architecture.

The system relies on internal APIs to establish clear contracts between its core components: the **UI**, **Logic**, 
**Model**, **Storage**, and **Security**. This document outlines the primary methods that facilitate data flow, 
command execution, and state management across these module boundaries. 

---

## 1. Logic API

The Logic component is the main execution engine of the application. It receives user action data from the UI, 
typically triggered through button clicks, with the required data types passed directly from the interface. 
The Logic layer validates permissions using the Session for role-based access control, ensuring the user has the 
correct authorization to perform the requested action. Upon validation, it coordinates interactions with both 
the Model and Storage components to update the application state and ensure data persistence, triggering a save 
operation through Storage after each successful operation.

### `Logic.authenticate(String password, Role role)`
* **Description:** Verifies the entered password against the stored BCrypt hash and, if successful, initializes the session for the selected role.
* **Parameters / inputs:** 
  * `password` (String): The plain text password entered in the UI.
  * `role` (Role): The requested operating role (e.g., `FIELD_MEDIC`, `MEDICAL_OFFICER`).
* **Return values:** `boolean` - Returns `true` if authentication is successful, `false` otherwise.
* **Example usage:** `boolean isAuth = logic.authenticate("myPassword123", Role.FIELD_MEDIC);`

### `Logic.addSupply(String name, int quantity, LocalDate expiryDate)`
* **Description:** Receives validated input from the "Add Supply" UI modal, creates a `Supply` object, updates the Model, and triggers a Storage save.
* **Parameters / inputs:** 
  * `name` (String): The name of the supply.
  * `quantity` (int): The amount of the supply.
  * `expiryDate` (LocalDate): The expiration date from the UI date picker.
* **Return values:** `void`
* **Example usage:** `logic.addSupply("Bandages", 50, LocalDate.of(2026, 12, 1));`

---

## 2. Model API

The Model component manages the in-memory state of the application, handling the Medical Inventory, 
the Personnel Roster, and the active User Session.

### `Model.setSessionRole(Role currentRole)`
* **Description:** Sets the active role for the current application session, which dictates which UI views and actions are accessible.
* **Parameters / inputs:** 
  * `currentRole` (Role): The authenticated role.
* **Return values:** `void`
* **Example usage:** `model.setSessionRole(Role.MEDICAL_OFFICER);`

### `Model.generateResupplyReport()`
* **Description:** Analyzes the inventory and generates a report flagging items with a quantity below 20 or an expiry within 30 days.
* **Parameters / inputs:** None.
* **Return values:** `List<ReportItem>` - A structured list containing the flagged items and the reason they were flagged.
* **Example usage:** `List<ReportItem> report = model.generateResupplyReport();`

### `Model.getFilteredPersonnelList()`
* **Description:** Retrieves the list of personnel based on the active session's filters (e.g., showing only `FIT` personnel for the Medical Officer).
* **Parameters / inputs:** None.
* **Return values:** `ObservableList<Personnel>` - A list that automatically updates the UI table.
* **Example usage:** `personnelTable.setItems(model.getFilteredPersonnelList());`

---

## 3. Storage API

The Storage component handles reading from and writing to the local hard drive, ensuring data (including 
security credentials) persists between application sessions without relying on an external database.

### `Storage.readMediTrackData()`
* **Description:** Reads the local JSON file during startup to load the saved inventory, roster data, and 
the application's master BCrypt password hash into memory.
* **Parameters / inputs:** None.
* **Return values:** `Optional<ReadOnlyMediTrack>` - Returns the parsed data if the file exists and is valid, or 
an empty Optional if no previous save data is found.
* **Example usage:** `Optional<ReadOnlyMediTrack> data = storage.readMediTrackData();`

### `Storage.saveMediTrackData(ReadOnlyMediTrack data)`
* **Description:** Serializes the current state of the application (inventory, roster, and password hash) 
and saves it to the local JSON file.
* **Parameters / inputs:** 
  * `data` (ReadOnlyMediTrack): A read-only snapshot of the current Model data.
* **Return values:** `void` (Throws `IOException` if the file cannot be written).
* **Example usage:** `storage.saveMediTrackData(model.getMediTrack());`

---

## 4. Security API

The Security component manages application authentication and password hashing, ensuring that sensitive credentials 
are not processed directly by general logic classes.

### `SecurityManager.authenticate(String plainTextPassword, String storedHash)`
* **Description:** Compares the plain text password entered by the user at launch against the BCrypt hash stored in the local data file.
* **Parameters / inputs:** 
  * `plainTextPassword` (String): The password entered in the UI.
  * `storedHash` (String): The BCrypt hash retrieved from Storage.
* **Return values:** `boolean` - Returns `true` if the password matches the hash, `false` otherwise.
* **Example usage:** `boolean isAuth = securityManager.authenticate(inputPassword, savedHash);`