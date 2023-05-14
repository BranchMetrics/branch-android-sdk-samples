# branch-Android-sample-test-app

Sample Test App for Branch Android SDK

## Purpose

The purpose of this app is to assist developers integrating the Branch Android SDK.
It provides simple use cases that integrate most of the Android Advanced Features found here: https://help.branch.io/developers-hub/docs/android-advanced-features

### *Features Implemented*
- Create a Content Reference
- Create Deep Link
- Share Deep Link
- Read Deep Link
- Create QR Code
- Navigate to Content
- Track Users
- Track Events
- Handle Links in Your Own App
- Set Initialization Metadata

### *How to Route to Content*
1. Build and run the app on your device or emulator
2. Click the share button on the top of the Home Page
3. Send the link to Messages, or copy and paste it into a text editor
4. Click on the Branch Link. The app should open and route to the page it was coded for.
5. (Optional) Test again by modifying link information in the shareBranchLink() function

## Home Page:

- App Creates a Branch Link on App Load at the top of the screen

- Send Branch Purchase Event, Add To Cart Event, and Change Background Color Event using the Buttons

- Create a QR Code / Share Branch Link with Navigation Buttons

- Share button creates a link that modifies the color block page

- QR Code button dims screen. Click anywhere on screen to hide QR Code

- Change Branch Badge button changes the badge to a version with opposite colors

## Color Block Page:

- Color block changes based on "color_block_key" parameter

- Options for "color_block_key" parameter are "red", "green", "blue", “yellow”, and “white”

- If one of the above 5 colors aren't selected, color block defaults to white

## Read Deep Link Page:

- Collects session parameters and stores them

- If a Branch link is clicked to open the app, session parameters are modified to match link parameters.

- Collects install parameters on download and stores them

- Page is scrollable in case parameters are too long for the screen

## Limitations

- Code changes are needed to modify link data on QR Code, Share Sheet, and Generated Branch Link

- Install Parameters are difficult to modify

- “+branch_force_new_session” may be confusing to developers on first use
