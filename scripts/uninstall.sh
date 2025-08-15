#!/bin/bash
echo "Uninstalling Sequence Board Game..."

# Remove installation directory
rm -rf "$HOME/SequenceGame"

# Remove global command
if [[ "$OSTYPE" == "darwin"* ]]; then
    rm -f /usr/local/bin/sequence-game
else
    rm -f ~/.local/bin/sequence-game
    rm -f ~/.local/share/applications/sequence-game.desktop
fi

echo "Uninstallation complete!"