set -e
FILE_NAME="$1"
BASE_NAME="${FILE_NAME%.*}"
O_FILE="$BASE_NAME.o"
NASM_FILE="$BASE_NAME.asm"
EXE_FILE="$BASE_NAME.exe"
nasm -felf64 -o "$O_FILE" "$NASM_FILE"
gcc -o "$EXE_FILE" -O0 --static -fno-pie -no-pie "$O_FILE"
rm "$O_FILE"
