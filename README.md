  Purrse: This Mobile Application is a Personal Expense Tracking App.
 Purpose of the App:
Purrse is a mobile expense tracking app designed to help users monitor their spending habits, set monthly goals, and make smarter financial decisions. It also allows users to add expenses by category, track their progress against set goals, and visualize spending trends through interactive graphs. 
This Application was built as part of our Open-Source Programming module and reflects feedback from a working prototype. It is optimized to run on an actual mobile device (Android), and not on an emulator anymore. 
 Design Considerations:
•	User-friendly and user-centred design
•	Simple and intuitive navigation
•	Clean UI with accessible colour schemes
•	Responsive and seamless layout for different screen sizes
•	Real-time validation for forms

 Feature-Driven Layout:
•	Home Screen: Overview of spending and quick action buttons. 
•	Add Expense Screen: Fast input with dropdowns and a date picker.
•	Add New Expense Category Screen: Allows users to add a custom expense category to organise their spending in a better and more efficient way. 
•	Category Management Screen: Allows users to manage their expense categories. Users can search existing categories, and view them in a scrollable list, as well as add new ones.
•	Goals Activity Screen: Lets users view and manage their spending goals, including their minimum and maximum budget limits for different categories or time periods. 
•	Login Screen: This is the users entry point into the app, allowing them secure access using a username and password. It also includes navigation to account creation. 
•	Main Activity Screen: This screen is the main dashboard or the Home page after a successful login from the user. It was designed to give users access to the core functionality of the app. 
•	Register Screen: Allows new users to create an account. UI is user-friendly, visually appealing and includes essential input validation opportunities. 
•	Welcome Screen: This is the first screen that users will see when they launch the app. It serves as a welcome screen and introduces users to the app in a friendly, visually appealing way before navigating them to the login or registration page. 

 Data and Security:
•	RealTime data storage (Firebase)
•	User authentication and session management
•	safe input handling and validation
 Core Features:
•	Add, edit, and delete expense records
•	View expenses by category
•	Set monthly minimum and maximum spending goals
•	Visual feedback on whether the user is within their budget goals
•	Interactive graphs showing spending trends over a selected period
•	Cloud database for persistent, multi-device access




 Custom-Added Features:
•	Monthly expenses: app gives a clear data table of the expenses for each month so uses can compare.
•	Savings: Users can see their savings based on the income and budgets
 Graphs and Visual Feedback:
Spending Graph: Displays total amount spent per category over a selectable data range.
Min/Max Goal Display: Shows goal lines on the graph for easy comparison.
Progress Bar/Feedback: A visual indicator (coloured progress bar and symbols) tells users if they are within the budget.
Example: 








 Online Data Storage:
•	All user data is stored in Firebase Realtime Database.
•	Data syncs automatically across sessions and devices.
•	Users can continue where they left off even after reinstalling the app.
 Automated Testing and GitHub Actions:
GitHub Actions is used for Continuous Integration. Every push runs a workflow that:
•	Builds the app.
•	Installs dependencies.
•	Runs automated tests to ensure the app functions correctly. 
 Demonstration Video:
Demo Video Link: 

Includes:
•	Real-time usage on an Android Phone.
•	Voice-over explanation.
•	Proof of data being saved and retrieved online.
•	Explanation of all of the apps features. 
 Technology/Tools Used:
Mobile UI: Android Studio
App Design: Figma
Database and Authentication: Firebase
Version Control: GitHub
Automated Testing: GitHub Actions
Video and Voice Over: 

 Authors:
Names: Faeeza Reynolds & Wadiha Boat
Student Numbers: st10314608 & st10303285
Institution: Varsity College
Module: PROG7313 (Open Source)

 Additional Notes:
•	Make sure that there is a stable internet connection for the online database features.
•	Graphs and goal tracking require at least some expenses data input.
•	For optimal performance, it is advised that Android 10 or later is to be used. 

 How to Run the App:
Prerequisites
•	Latest Version of Android Studio should be Installed.
•	Java Development Kit to be Installed (JDK).
•	Gradle (Integrated into Android Studio)
•	Android Emulator or Physical Android Device Connected. 

 Steps to Run the App:
•	Clone the Repository - using Android Studio directly. Or, download the ZIP file directly from GitHub and extract it.
•	Open the project in Android Studio: Open Android Studio, then select File, Click on Open and select the Purrse Folder. 
•	Click “Trust Project” if prompted.



 Syncing the Gradle:
Android Studio will automatically start to sync the project, but it can also be done manually:
•	Click “File”, then “Sync Project with Gradle Files”.
All the dependencies should load without any errors. 

 Setup Emulator or Connect a Device:
•	Run the App on an Android Device (Enable USB debugging)
•	To run on an emulator: Go to “Device Manager” in “Tools” and create or launch a virtual device.
 Build and Run the App:
Click “Build” and then “Make Project”
Select the connected device at the top
Press Shift + F10 or click the green run arrow 
The app will now launch on the device




 
How to Use the App:
Welcome Screen:
•	Tap “Continue” to enter the App.
Register Screen:
•	If you are a new user, enter a Username, Email, Password.
•	Tap “Register” to create your account.
Login Screen:
•	Enter your Username and Password.
•	Tap “Login” to access the dashboard.
Category Management:
•	View summary of expenses, balance, savings and income where users can enter their incomes.
•	Shows top spending categories 
•	Bottom navigation to go to different screens
Expense Tracking:
•	Add expenses under specific categories.
•	Each expense includes:
•	Amount
•	Category
•	Date
•	Description
•	View your expenses in a list format.

 Goals Page:
•	For assigning total budget for month
•	Assign budget for categories
•	Progress bar of remaining budget for each category

 Extra Additional Notes:
•	Data is stored locally in the app’s database.
•	User-friendly UI design for ease of use and efficient accessibility.
•	Optimized for budgeting and daily expense tracking. 
