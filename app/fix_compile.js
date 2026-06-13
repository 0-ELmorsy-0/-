const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
let code = fs.readFileSync(file, 'utf8');

// 1. `viewModel.updateProfileData` -> no-op or we can just use `viewModel.updatePatientProfile`
// The original update was:
/*
viewModel.updatePatientProfile(
                            name = editName,
                            phone = editPhone,
                            address = editAddress,
                            age = editAge,
                            bloodType = editBloodType,
                            weight = editWeight,
                            gender = editGender,
                            conditions = editConditions,
                            allergies = editAllergies,
                            emergencyName = editEmergencyName,
                            emergencyPhone = editEmergencyPhone,
                            heartRate = editHeartRate, // oh wait we don't need this
                            ...
*/
// Inside EditMedicalProfileScreen we added:
code = code.replace(
    /viewModel\.updateProfileData\(.*?\)\s*viewModel\.updateMedicalProfile\([^)]+\)/s,
    `viewModel.updatePatientProfile(
                        name = editName,
                        phone = editPhone,
                        address = editAddress,
                        age = editAge,
                        bloodType = editBloodType,
                        weight = editWeight,
                        gender = editGender,
                        conditions = editConditions,
                        allergies = editAllergies,
                        emergencyName = editEmergencyName,
                        emergencyPhone = editEmergencyPhone,
                        heartRate = viewModel.recentHeartRate.value,
                        bloodPressure = viewModel.recentBloodPressure.value,
                        bloodSugar = viewModel.recentBloodSugar.value
                    )`
);

// 2. `t.description` -> `t.title`
code = code.replace(/Text\(t\.description\)/g, 'Text(t.title)');
// `t.type == "CREDIT"` -> `t.isCredit`
code = code.replace(/t\.type == "CREDIT"/g, 't.isCredit');

// 3. `uploadMedicalDocument` -> `addMedicalDocument`
code = code.replace(/viewModel\.uploadMedicalDocument/g, 'viewModel.addMedicalDocument');

// 4. `logVitalSign` -> `addVitalLog`
code = code.replace(/viewModel\.logVitalSign\((.*?),\s*(.*?),\s*(.*?)\)/g, 'viewModel.addVitalLog($1, $2, $3)');

// 5. In VitalsDashboardScreen, we also had `updateMedicalProfile` which is now `updatePatientProfile`
// Wait, `patientAge` is a state flow? Yes by viewModel.patientAge.collectAsState(). But in updatePatientProfile we need the string value.
code = code.replace(
    /viewModel\.updateMedicalProfile\([^)]+\)/g,
    `viewModel.updatePatientProfile(
                    name = viewModel.userName.value,
                    phone = viewModel.userPhone.value,
                    address = viewModel.userAddress.value,
                    age = patientAge,
                    bloodType = patientBloodType,
                    weight = patientWeight,
                    gender = patientGender,
                    conditions = patientConditions,
                    allergies = patientAllergies,
                    emergencyName = emergencyContactName,
                    emergencyPhone = emergencyContactPhone,
                    heartRate = if(newVitalType == "HEART") newValue else recentHeartRate,
                    bloodPressure = if(newVitalType == "PRESSURE") newValue else recentBloodPressure,
                    bloodSugar = if(newVitalType == "SUGAR") newValue else recentBloodSugar
                )`
);

// 6. `rem.medicationName` -> `rem.medName`
code = code.replace(/rem\.medicationName/g, 'rem.medName');

// 7. `Icons.AutoMirrored.Filled.Send` -> `Icons.AutoMirrored.Filled.Send` (it should exist). Wait, the error said `Icons.Filled.Send` unresolved. Oh, because I used `Icons.AutoMirrored.Filled.Send` in the code, wait, let me check the error. 
// "val Icons.Filled.Send: ImageVector" It means it thinks it's `Icons.Filled.Send`. Let's just use `Icons.Default.Send` if it exists. 
// Actually `Icons.AutoMirrored.Filled.Send` exists in some Compose versions. `Icons.Default.Send` exists. Let's use `Icons.Default.Send` if `Icons.AutoMirrored.Filled.Send` fails, wait, the error was:
// `val Icons.Filled.Send: ImageVector`
// I replaced `Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)` ... Wait, let's just use `Icons.AutoMirrored.Filled.Send` if there is `Send`. But `Send` is auto-mirrored. `Icons.Default.Send` exists.
code = code.replace(/Icons\.AutoMirrored\.Filled\.Send/g, 'Icons.AutoMirrored.Filled.Send'); // I used AutoMirrored initially? No I said `Icons.AutoMirrored.Filled.Send` in my script. 
// The error says "val Icons.Filled.Send". In compose, sometimes AutoMirrored delegates to filled.
code = code.replace(/Icons\.AutoMirrored\.Filled\.Send/g, 'androidx.compose.material.icons.automirrored.filled.Send');

fs.writeFileSync(file, code);
console.log("Fixes applied.");

