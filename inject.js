const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
const code = fs.readFileSync(file, 'utf8');
const uiCode = fs.readFileSync('uicode.txt', 'utf8');

const newCode = code.replace('        // REPLACEME_UI_HERE', uiCode);

fs.writeFileSync(file, newCode);
console.log('UI injected.');
