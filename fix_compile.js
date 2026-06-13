const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
let code = fs.readFileSync(file, 'utf8');

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

code = code.replace(/Text\(t\.description\)/g, 'Text(t.title)');
code = code.replace(/t\.type == "CREDIT"/g, 't.isCredit');
code = code.replace(/viewModel\.uploadMedicalDocument/g, 'viewModel.addMedicalDocument');
code = code.replace(/viewModel\.logVitalSign\((.*?),\s*(.*?),\s*(.*?)\)/g, 'viewModel.addVitalLog($1, $2, $3)');

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

code = code.replace(/rem\.medicationName/g, 'rem.medName');

fs.writeFileSync(file, code);
console.log("Fixes applied.");
