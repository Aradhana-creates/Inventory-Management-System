Software Requirements Specification (SRS)

Inventory Management System



Software Engineering Laboratory
Project Name: Inventory Management System
Version: 1.0
Prepared By: ARADHANA.P
Department: INFORMATION TECHNOLOGY


 1. Introduction
1.1 Purpose
The purpose of this Software Requirements Specification (SRS) document is to describe the functional and non-functional requirements of the Inventory Management System. The system aims to automate the monitoring of inventory levels, purchase orders, suppliers, stock movements, warehouse operations, and product sales. This document serves as a reference for developers, testers, and stakeholders throughout the software development life cycle.
1.2 Scope
The Inventory Management System is designed to simplify inventory control by replacing manual record keeping with an automated system. The software allows warehouse staff and managers to maintain product records, track stock levels in real time, manage purchase orders and suppliers, record stock movements between warehouses, and monitor product sales.
The system will support:
•	Product & Inventory Management
•	Purchase Order Management
•	Supplier Management
•	Stock Movement Tracking
•	Warehouse Operations
•	Sales Management
•	Report Generation
•	User Authentication
1.3 Definitions
Term	Description
Admin	Inventory / System Administrator
Warehouse Staff	User responsible for stock handling and movement
SKU	Stock Keeping Unit — unique identifier for a product
Reorder Level	Minimum stock quantity that triggers a replenishment order
Purchase Order (PO)	Formal request sent to a supplier to procure stock
Database	Repository for storing inventory information
1.4 References
•	IEEE 830 Software Requirements Specification Standard
•	Software Engineering by Ian Sommerville
•	Software Engineering by Roger S. Pressman
2. Overall Description
2.1 Product Perspective
The Inventory Management System is a standalone desktop/web application that interacts with a centralized database. It is used by warehouse and business staff to coordinate stock, procurement, and sales activities across one or more warehouses.
Users include:
•	Administrator
•	Inventory/Warehouse Manager
•	Warehouse Staff
•	Sales Staff
•	Supplier (external, via purchase orders)
2.2 Product Functions
•	User Login
•	Add/Edit/Delete Products
•	Manage Suppliers
•	Create & Track Purchase Orders
•	Record Stock Movements (In/Out/Transfer)
•	Manage Warehouses & Locations
•	Record Product Sales
•	Low-Stock / Reorder Alerts
•	Generate Reports
•	Change Password
•	Logout
2.3 User Characteristics
Administrator
•	Manage users
•	Manage products, warehouses, and suppliers
•	View all reports
Inventory/Warehouse Manager
•	Approve purchase orders
•	Monitor stock levels
•	Manage warehouse operations
Warehouse Staff
•	Record incoming and outgoing stock
•	Perform stock transfers between warehouses
•	Conduct stock counts
Sales Staff
•	Record product sales
•	View product availability
2.4 Operating Environment
•	Windows 10/11
•	Linux
•	Java JDK 17
•	MySQL Database
•	Eclipse/IntelliJ IDEA
•	Apache Tomcat (for Web Application)
2.5 Design Constraints
•	Java Programming Language
•	MySQL Database
•	Password-based authentication
•	Maximum response time less than 3 seconds
3. Specific Requirements
3.1 Functional Requirements
FR1 User Login
Description: The system shall authenticate users using username and password, and route them to role-specific dashboards.
Input: Username, Password
Output: Login Successful / Invalid Credentials
FR2 Product Management
The admin/manager shall be able to:
•	Add Product
•	Update Product
•	Delete Product
•	Search Product
•	Assign SKU and Reorder Level
FR3 Supplier Management
The system shall:
•	Register new suppliers
•	Edit supplier details
•	Delete supplier information
•	Link suppliers to purchase orders
FR4 Purchase Order Management
The manager shall:
•	Create a purchase order to a supplier
•	Approve or reject a purchase order
•	Track purchase order status (Pending/Approved/Received)
•	Update stock upon order receipt
FR5 Stock Movement Tracking
The warehouse staff shall:
•	Record stock inward entries
•	Record stock outward entries
•	Transfer stock between warehouses
•	View stock movement history
FR6 Warehouse Operations
The system shall:
•	Maintain multiple warehouse locations
•	Track stock quantity per warehouse
•	Support stock count / audit entries
FR7 Sales Management
The sales staff shall:
•	Record a product sale
•	Reduce stock automatically upon sale
•	Generate sales invoice
FR8 Alerts & Reports
Generate alerts and reports for:
•	Low-Stock / Reorder Alerts
•	Current Stock Levels
•	Purchase Order History
•	Stock Movement Log
•	Sales Summary
3.2 Non-Functional Requirements
Performance
•	Response time should be less than 3 seconds.
Reliability
•	System availability should be 99%.
Security
•	Password authentication.
•	Role-based access.
•	Encrypted passwords.
Usability
•	User-friendly interface.
•	Easy navigation.
•	Error messages for invalid input.
Maintainability
•	Modular design.
•	Easy code maintenance.
•	Well-documented source code.
Scalability
•	Support up to 100,000 product records.
•	Support multiple warehouses and 500+ suppliers.
4. External Interface Requirements
4.1 User Interface
•	Login Screen
•	Dashboard
•	Product Management Screen
•	Supplier Management Screen
•	Purchase Order Screen
•	Stock Movement Screen
•	Warehouse Management Screen
•	Sales Screen
•	Reports Screen
4.2 Hardware Interface
•	Desktop/Laptop
•	Barcode Scanner (Optional)
•	Printer (for invoices and reports)
4.3 Software Interface
•	Java
•	MySQL
•	JDBC Driver
•	Apache Tomcat
5. System Features
Feature 1: Product Management
Inputs: Product ID, SKU, Name, Category, Unit Price, Reorder Level
Outputs: Product Added Successfully
Feature 2: Purchase Order
Inputs: Supplier ID, Product ID, Quantity, Order Date
Outputs: Purchase Order Created Successfully
Feature 3: Stock Movement
Inputs: Product ID, Warehouse ID, Movement Type (In/Out/Transfer), Quantity
Outputs: Stock Updated Successfully
Feature 4: Sales Recording
Inputs: Product ID, Quantity Sold, Sale Date, Customer Details
Outputs: Sale Recorded, Invoice Generated
Feature 5: Low-Stock Alert
Inputs: Product ID, Current Stock, Reorder Level
Outputs: Reorder Alert Notification
6. Data Requirements
Product Table
Field	Type
Product_ID	Integer
SKU	Varchar
Name	Varchar
Category	Varchar
Unit_Price	Double
Reorder_Level	Integer

Supplier Table
Field	Type
Supplier_ID	Integer
Name	Varchar
Contact_Person	Varchar
Phone	Varchar
Email	Varchar

Purchase Order Table
Field	Type
PO_ID	Integer
Supplier_ID	Integer
Product_ID	Integer
Quantity	Integer
Order_Date	Date
Status	Varchar

Warehouse Table
Field	Type
Warehouse_ID	Integer
Name	Varchar
Location	Varchar
Capacity	Integer

Stock Movement Table
Field	Type
Movement_ID	Integer
Product_ID	Integer
Warehouse_ID	Integer
Movement_Type	Varchar
Quantity	Integer
Movement_Date	Date

Sales Table
Field	Type
Sale_ID	Integer
Product_ID	Integer
Quantity_Sold	Integer
Sale_Date	Date
Total_Amount	Double
7. Assumptions
•	Internet connection is available (for web version).
•	Database server is operational.
•	Only registered users can access the system.
•	Suppliers provide accurate product and pricing information.
8. Constraints
•	Stock cannot be sold or transferred below available quantity.
•	Duplicate SKUs are not allowed.
•	Purchase orders below the reorder threshold require manager approval.
•	Stock movement records cannot be deleted once confirmed, only reversed via a new entry.
9. Acceptance Criteria
The software will be considered successful if it:
•	Authenticates valid users.
•	Allows product, supplier, and warehouse management.
•	Tracks purchase orders and stock movements correctly.
•	Records sales and updates stock levels accurately.
•	Generates low-stock alerts and reports without errors.
•	Maintains data consistency and security.
