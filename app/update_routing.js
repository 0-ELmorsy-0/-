const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
let code = fs.readFileSync(file, 'utf8');

const routesAddition = `
                            "wallet" -> WalletScreen(viewModel)
                            "edit_medical_profile" -> EditMedicalProfileScreen(viewModel)
                            "vitals_dashboard" -> VitalsDashboardScreen(viewModel)
                            "medical_docs" -> MedicalDocsScreen(viewModel)
                            "medication_reminders" -> MedicationRemindersScreen(viewModel)
                            "support_chat" -> SupportChatScreen(viewModel)
`;

code = code.replace('"nurse_details" -> NurseDetailsScreen(viewModel)', '"nurse_details" -> NurseDetailsScreen(viewModel)' + routesAddition);

// Now update ProfileScreen click listeners
code = code.replace('showRechargeDialog = true', 'viewModel.navigateTo("wallet")');
code = code.replace('showVitalHistoryDialog = true', 'viewModel.navigateTo("vitals_dashboard")');
code = code.replace('showAddDocDialog = true', 'viewModel.navigateTo("medical_docs")');
code = code.replace('showAddReminderDialog = true', 'viewModel.navigateTo("medication_reminders")');
code = code.replace('viewModel.initSupportChat(); showSupportChatOverlay = true', 'viewModel.navigateTo("support_chat")');

// Replace the edit profile click listener
// it has variables assigning, we can either leave them or just remove them
code = code.replace(/editName = userName; editPhone = userPhone;.*?showEditDialog = true/, 'viewModel.navigateTo("edit_medical_profile")');

fs.writeFileSync(file, code);

console.log("Routing updated.");
