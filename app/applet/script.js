const fs = require('fs');
const file = 'app/src/main/java/com/example/ui/screens/ELmorsyAppContent.kt';
const lines = fs.readFileSync(file, 'utf8').split('\n');

const newLines = [
    ...lines.slice(0, 7006),
    '        // REPLACEME_UI_HERE',
    ...lines.slice(8345)
];

fs.writeFileSync(file, newLines.join('\n'));
console.log('Done modifying lines.');
