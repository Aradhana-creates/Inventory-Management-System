SOFTWARE PROJECT PLAN (SPP)
Project Title
Inventory Management System
1. Introduction
1.1 Purpose
The purpose of this project is to develop an Inventory Management System (IMS) that automates inventory operations such as product management, stock tracking, supplier management, purchase and sales records, low-stock alerts, and report generation. The system aims to reduce manual work, prevent stock-related errors, improve efficiency, and provide quick access to accurate inventory information.
1.2 Project Objectives
•	Automate inventory operations.
•	Maintain accurate product and stock records.
•	Manage supplier and purchase details.
•	Track stock-in and stock-out transactions.
•	Provide low-stock alerts and reorder information.
•	Generate inventory and transaction reports.
•	Improve data accuracy, security, and decision-making.
1.3 Scope
•	Admin Login
•	Staff/User Login
•	Product Management
•	Category Management
•	Supplier Management
•	Stock In / Purchase Management
•	Stock Out / Sales Management
•	Search and Filter Products
•	Low-Stock Alerts
•	Inventory Reports
•	Database Backup
2. Feasibility Study
Technical Feasibility
•	Java, MySQL, and Eclipse/NetBeans can be used for development.
•	Existing computers are sufficient for development and deployment.
•	The system can be implemented using commonly available software tools.
Economic Feasibility
•	Open-source development tools reduce software costs.
•	Existing hardware can be used.
•	Maintenance and operating expenses are expected to be minimal.
Operational Feasibility
•	Simple and user-friendly interface.
•	Requires minimal training for staff.
•	Reduces manual inventory calculations and paperwork.
Schedule Feasibility
•	The system can be completed within 12 weeks with planned development and testing.
3. Software Development Life Cycle (SDLC)
The project follows the Waterfall Model.
Requirement Analysis
        ↓
System Design
        ↓
Implementation
        ↓
Testing
        ↓
Deployment
        ↓
Maintenance
4. Project Organization
Role	Responsibility
Project Manager	Overall project planning and management
System Analyst	Requirement analysis and documentation
Designer	Database and user-interface design
Developer	Coding and module implementation
Tester	Testing and defect identification
Inventory Manager/User	User acceptance testing and feedback
5. Functional Requirements
Administrator
•	Login securely.
•	Add, edit, and delete products.
•	Manage categories and suppliers.
•	Manage users/staff.
•	View inventory and transaction reports.
•	Monitor low-stock products.
Inventory Staff
•	Search and view products.
•	Record stock received.
•	Record stock issued/sold.
•	Update product quantities.
•	View supplier and purchase details.
•	Check low-stock alerts.
Manager
•	Login.
•	View current stock levels.
•	View stock movement.
•	View sales/purchase summaries.
•	View inventory reports.
•	Monitor low-stock and reorder requirements.
6. Non-Functional Requirements
•	Fast response time (target below 3 seconds for normal operations).
•	Secure login and role-based access.
•	Reliable database storage and backup.
•	Easy maintenance and scalability.
•	Accurate stock calculations.
•	User-friendly interface.
•	System availability and data integrity.
7. Hardware Requirements
•	Intel Core i3 processor or above
•	8 GB RAM
•	500 GB HDD/SSD
•	Keyboard and mouse
•	Printer (optional)
•	Barcode scanner (optional)
8. Software Requirements
Software	Version / Requirement
Operating System	Windows / Linux
Java	JDK 17
IDE	Eclipse / NetBeans
Database	MySQL
Web Server	Apache Tomcat (if web-based)
9. Work Breakdown Structure (WBS)
Inventory Management System
│
├── Requirement Analysis
│   ├── Study Existing Inventory Process
│   ├── Requirement Gathering
│   └── Documentation
│
├── Design
│   ├── ER Diagram
│   ├── Database Design
│   ├── UML Diagrams
│   └── UI Design
│
├── Development
│   ├── Login Module
│   ├── Product Module
│   ├── Category Module
│   ├── Supplier Module
│   ├── Stock In Module
│   ├── Stock Out Module
│   └── Report Module
│
├── Testing
│   ├── Unit Testing
│   ├── Integration Testing
│   ├── System Testing
│   └── User Acceptance Testing
│
└── Deployment
    ├── Installation
    ├── User Training
    └── Maintenance
10. Project Schedule (12 Weeks)
Week	Activity
1	Requirement Collection
2	Feasibility Study
3	System Design
4	Database Design
5	User Interface Design
6	Login and User Management Module
7	Product and Category Management
8	Supplier and Purchase/Stock-In Module
9	Stock-Out and Inventory Tracking Module
10	Reports and Low-Stock Alerts
11	Testing
12	Deployment & Documentation
11. Gantt Chart
Activity	W1	W2	W3	W4	W5	W6	W7	W8	W9	W10	W11	W12
Requirement Analysis	█	█										
Feasibility Study		█										
Design			█	█	█							
Coding					█	█	█	█	█	█		
Testing											█	
Deployment												█
12. Risk Analysis
Risk	Impact	Mitigation
Requirement Changes	High	Freeze major requirements early and document changes.
Developer Absence	Medium	Share knowledge and maintain proper documentation.
Database Failure	High	Perform regular backups and recovery testing.
Incorrect Stock Updates	High	Validate every stock transaction and use role-based access.
Schedule Delay	Medium	Monitor weekly progress and prioritize critical modules.
Software Bugs	High	Perform frequent testing throughout development.
13. Cost Estimation
Item	Estimated Cost (₹)
Software Development	50,000
Hardware	30,000
Database	10,000
Documentation	5,000
Testing	5,000
Total Estimated Cost	1,00,000
14. Quality Assurance Plan
•	Code reviews
•	Unit testing
•	Integration testing
•	System testing
•	User Acceptance Testing (UAT)
•	Performance testing
•	Database backup and recovery testing
•	Validation of stock calculations
15. Test Plan
Test Case	Expected Result
Login	Valid user logs in successfully.
Add Product	Product is added to the database successfully.
Update Product	Updated product details are saved correctly.
Search Product	Correct product information is displayed.
Stock In	Stock quantity increases correctly.
Stock Out	Stock quantity decreases correctly.
Low-Stock Alert	Alert is displayed when stock reaches the defined minimum level.
Generate Report	Inventory report is generated successfully.
16. Deliverables
•	Software Requirements Specification (SRS)
•	Software Project Plan (SPP)
•	UML Diagrams
•	ER Diagram and Database Design
•	Source Code
•	Test Cases and Test Reports
•	User Manual
•	Final Project Report
•	Inventory Management Software
17. Expected Outcomes
•	Reduced manual paperwork.
•	Faster stock entry and retrieval.
•	Accurate inventory tracking.
•	Reduced risk of stock shortages and overstocking.
•	Better supplier and purchase management.
•	Automatic low-stock alerts.
•	Improved reporting and decision-making.
•	Better data security and record management.
SDLC Phases Summary
SDLC Phase	Deliverable
Planning	Project Plan
Requirement Analysis	SRS Document
Design	UML, ER Diagram, Database Design
Implementation	Source Code
Testing	Test Cases & Reports
Deployment	Installed Software
Maintenance	Bug Fixes & Updates

