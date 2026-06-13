const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
let code = fs.readFileSync(file, 'utf8');

code = code.replace(
    '        Surface(\n            modifier = Modifier\n                .fillMaxWidth()\n                .height(72.dp),\n            color = Color(0xFF0A0A0A), // Premium Dark\n            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)\n        ) {',
    '        Surface(\n            modifier = Modifier\n                .fillMaxWidth()\n                .height(72.dp)\n                .shadow(elevation = 16.dp),\n            color = Color.White\n        ) {'
);

code = code.replace(
    'val iconColor = if (isActive) Color.White else Color(0xFF888888)\n                        val textColor = if (isActive) Color.White else Color(0xFF888888)',
    'val iconColor = if (isActive) MedicalBlue else Color(0xFF888888)\n                        val textColor = if (isActive) MedicalBlue else Color(0xFF888888)'
);

code = code.replace(
    '                .border(\n                    width = 4.dp,\n                    color = Color(0xFF0A0A0A), // Matches the dark bar to create a pseudo-cutout\n                    shape = CircleShape\n                )',
    '                .border(\n                    width = 4.dp,\n                    color = Color.White,\n                    shape = CircleShape\n                )\n                .shadow(elevation = 8.dp, shape = CircleShape)'
);

fs.writeFileSync(file, code);
console.log('Fixed Nav bar');
