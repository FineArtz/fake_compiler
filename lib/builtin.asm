





default rel

global print
global println
global printInt
global printlnInt
global getString
global getInt
global toString
global str_concat
global str_equal
global str_not_equal
global str_less
global str_lte
global parseInt
global ord
global substring
global REG_SIZE

extern strcmp
extern getchar
extern strlen
extern __isoc99_scanf
extern malloc
extern __stack_chk_fail
extern putchar
extern puts
extern printf
extern _GLOBAL_OFFSET_TABLE_


SECTION .text   

print:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     rsi, rax
        lea     rdi, [rel L_054]
        mov     eax, 0
        call    printf
        nop
        leave
        ret


println:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     rdi, rax
        call    puts
        nop
        leave
        ret


printInt:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 80
        mov     dword [rbp-44H], edi


        mov     rax, qword [fs:abs 28H]
        mov     qword [rbp-8H], rax
        xor     eax, eax
        cmp     dword [rbp-44H], 0
        jnz     L_001
        mov     edi, 48
        call    putchar
        jmp     L_002

L_001:  cmp     dword [rbp-44H], 0
        jns     L_002
        neg     dword [rbp-44H]
        mov     edi, 45
        call    putchar
L_002:  mov     dword [rbp-38H], 0
        jmp     L_004

L_003:  mov     esi, dword [rbp-38H]
        lea     eax, [rsi+1H]
        mov     dword [rbp-38H], eax
        mov     ecx, dword [rbp-44H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        shl     eax, 2
        add     eax, edx
        add     eax, eax
        sub     ecx, eax
        mov     edx, ecx
        movsxd  rax, esi
        mov     dword [rbp+rax*4-30H], edx
        mov     ecx, dword [rbp-44H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        mov     dword [rbp-44H], eax
L_004:  cmp     dword [rbp-44H], 0
        jg      L_003
        mov     eax, dword [rbp-38H]
        sub     eax, 1
        mov     dword [rbp-34H], eax
        jmp     L_006

L_005:  mov     eax, dword [rbp-34H]
        cdqe
        mov     eax, dword [rbp+rax*4-30H]
        add     eax, 48
        mov     edi, eax
        call    putchar
        sub     dword [rbp-34H], 1
L_006:  cmp     dword [rbp-34H], 0
        jns     L_005
        nop
        mov     rax, qword [rbp-8H]


        xor     rax, qword [fs:abs 28H]
        jz      L_007
        call    __stack_chk_fail
L_007:  leave
        ret


printlnInt:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 80
        mov     dword [rbp-44H], edi


        mov     rax, qword [fs:abs 28H]
        mov     qword [rbp-8H], rax
        xor     eax, eax
        cmp     dword [rbp-44H], 0
        jnz     L_008
        mov     edi, 48
        call    putchar
        jmp     L_009

L_008:  cmp     dword [rbp-44H], 0
        jns     L_009
        neg     dword [rbp-44H]
        mov     edi, 45
        call    putchar
L_009:  mov     dword [rbp-38H], 0
        jmp     L_011

L_010:  mov     esi, dword [rbp-38H]
        lea     eax, [rsi+1H]
        mov     dword [rbp-38H], eax
        mov     ecx, dword [rbp-44H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        shl     eax, 2
        add     eax, edx
        add     eax, eax
        sub     ecx, eax
        mov     edx, ecx
        movsxd  rax, esi
        mov     dword [rbp+rax*4-30H], edx
        mov     ecx, dword [rbp-44H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        mov     dword [rbp-44H], eax
L_011:  cmp     dword [rbp-44H], 0
        jg      L_010
        mov     eax, dword [rbp-38H]
        sub     eax, 1
        mov     dword [rbp-34H], eax
        jmp     L_013

L_012:  mov     eax, dword [rbp-34H]
        cdqe
        mov     eax, dword [rbp+rax*4-30H]
        add     eax, 48
        mov     edi, eax
        call    putchar
        sub     dword [rbp-34H], 1
L_013:  cmp     dword [rbp-34H], 0
        jns     L_012
        mov     edi, 10
        call    putchar
        nop
        mov     rax, qword [rbp-8H]


        xor     rax, qword [fs:abs 28H]
        jz      L_014
        call    __stack_chk_fail
L_014:  leave
        ret


getString:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     edi, 266
        call    malloc
        mov     qword [rbp-8H], rax
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     rsi, rax
        lea     rdi, [rel L_054]
        mov     eax, 0
        call    __isoc99_scanf
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     rdi, rax
        call    strlen
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     rax, qword [rbp-8H]
        leave
        ret


getInt:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        call    getchar
        mov     byte [rbp-9H], al
        jmp     L_016

L_015:  call    getchar
        mov     byte [rbp-9H], al
L_016:  cmp     byte [rbp-9H], 45
        jz      L_017
        cmp     byte [rbp-9H], 47
        jle     L_015
        cmp     byte [rbp-9H], 57
        jg      L_015
L_017:  cmp     byte [rbp-9H], 45
        jnz     L_018
        mov     eax, 4294967295
        jmp     L_019

L_018:  mov     eax, 1
L_019:  mov     dword [rbp-4H], eax
        cmp     byte [rbp-9H], 45
        jz      L_020
        movsx   eax, byte [rbp-9H]
        sub     eax, 48
        jmp     L_021

L_020:  mov     eax, 0
L_021:  mov     dword [rbp-8H], eax
        jmp     L_023

L_022:  mov     edx, dword [rbp-8H]
        mov     eax, edx
        shl     eax, 2
        add     eax, edx
        add     eax, eax
        mov     edx, eax
        movsx   eax, byte [rbp-9H]
        add     eax, edx
        sub     eax, 48
        mov     dword [rbp-8H], eax
L_023:  call    getchar
        mov     byte [rbp-9H], al
        cmp     byte [rbp-9H], 47
        jle     L_024
        cmp     byte [rbp-9H], 57
        jg      L_024
        mov     eax, 1
        jmp     L_025

L_024:  mov     eax, 0
L_025:  test    eax, eax
        jnz     L_022
        mov     eax, dword [rbp-8H]
        imul    eax, dword [rbp-4H]
        mov     dword [rbp-8H], eax
        mov     eax, dword [rbp-8H]
        leave
        ret


toString:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 96
        mov     dword [rbp-54H], edi


        mov     rax, qword [fs:abs 28H]
        mov     qword [rbp-8H], rax
        xor     eax, eax
        cmp     dword [rbp-54H], 0
        jns     L_026
        neg     dword [rbp-54H]
        mov     dword [rbp-44H], -1
        jmp     L_027

L_026:  mov     dword [rbp-44H], 0
L_027:  mov     dword [rbp-40H], 0
        cmp     dword [rbp-54H], 0
        jnz     L_029
        mov     eax, dword [rbp-40H]
        lea     edx, [rax+1H]
        mov     dword [rbp-40H], edx
        cdqe
        mov     dword [rbp+rax*4-30H], 0
        jmp     L_030

L_028:  mov     esi, dword [rbp-40H]
        lea     eax, [rsi+1H]
        mov     dword [rbp-40H], eax
        mov     ecx, dword [rbp-54H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        shl     eax, 2
        add     eax, edx
        add     eax, eax
        sub     ecx, eax
        mov     edx, ecx
        movsxd  rax, esi
        mov     dword [rbp+rax*4-30H], edx
        mov     ecx, dword [rbp-54H]
        mov     edx, 1717986919
        mov     eax, ecx
        imul    edx
        sar     edx, 2
        mov     eax, ecx
        sar     eax, 31
        sub     edx, eax
        mov     eax, edx
        mov     dword [rbp-54H], eax
L_029:  cmp     dword [rbp-54H], 0
        jg      L_028
L_030:  mov     edx, dword [rbp-44H]
        mov     eax, dword [rbp-40H]
        add     eax, edx
        mov     edx, eax
        mov     eax, 8
        add     eax, edx
        add     eax, 1
        mov     eax, eax
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-38H], rax
        mov     edx, dword [rbp-44H]
        mov     eax, dword [rbp-40H]
        add     eax, edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-38H]
        mov     qword [rax], rdx
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-38H], rax
        cmp     dword [rbp-44H], 0
        jz      L_031
        mov     rax, qword [rbp-38H]
        mov     byte [rax], 45
L_031:  mov     dword [rbp-3CH], 0
        jmp     L_033

L_032:  mov     eax, dword [rbp-40H]
        sub     eax, dword [rbp-3CH]
        sub     eax, 1
        cdqe
        mov     eax, dword [rbp+rax*4-30H]
        lea     ecx, [rax+30H]
        mov     edx, dword [rbp-44H]
        mov     eax, dword [rbp-3CH]
        add     eax, edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-38H]
        add     rax, rdx
        mov     edx, ecx
        mov     byte [rax], dl
        add     dword [rbp-3CH], 1
L_033:  mov     eax, dword [rbp-3CH]
        cmp     eax, dword [rbp-40H]
        jl      L_032
        mov     edx, dword [rbp-44H]
        mov     eax, dword [rbp-40H]
        add     eax, edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-38H]
        add     rax, rdx
        mov     byte [rax], 0
        mov     eax, 8
        mov     eax, eax
        neg     rax
        mov     rdx, rax
        mov     rax, qword [rbp-38H]
        add     rax, rdx
        mov     rdi, qword [rbp-8H]


        xor     rdi, qword [fs:abs 28H]
        jz      L_034
        call    __stack_chk_fail
L_034:  leave
        ret


str_concat:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 48
        mov     qword [rbp-28H], rdi
        mov     qword [rbp-30H], rsi
        mov     rax, qword [rbp-28H]
        mov     rax, qword [rax]
        mov     dword [rbp-14H], eax
        mov     rax, qword [rbp-30H]
        mov     rax, qword [rax]
        mov     dword [rbp-10H], eax
        mov     edx, dword [rbp-14H]
        mov     eax, dword [rbp-10H]
        add     eax, edx
        mov     edx, eax
        mov     eax, 8
        add     eax, edx
        add     eax, 1
        mov     eax, eax
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-8H], rax
        mov     edx, dword [rbp-14H]
        mov     eax, dword [rbp-10H]
        add     eax, edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-28H], rax
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-30H], rax
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-8H], rax
        mov     dword [rbp-0CH], 0
        mov     dword [rbp-18H], 0
        jmp     L_038

L_035:  mov     eax, dword [rbp-18H]
        cmp     eax, dword [rbp-14H]
        jge     L_036
        mov     eax, dword [rbp-18H]
        movsxd  rdx, eax
        mov     rax, qword [rbp-28H]
        add     rax, rdx
        movzx   eax, byte [rax]
        jmp     L_037

L_036:  mov     eax, dword [rbp-18H]
        sub     eax, dword [rbp-14H]
        movsxd  rdx, eax
        mov     rax, qword [rbp-30H]
        add     rax, rdx
        movzx   eax, byte [rax]
L_037:  mov     edx, dword [rbp-18H]
        movsxd  rcx, edx
        mov     rdx, qword [rbp-8H]
        add     rdx, rcx
        mov     byte [rdx], al
        add     dword [rbp-18H], 1
L_038:  mov     edx, dword [rbp-14H]
        mov     eax, dword [rbp-10H]
        add     eax, edx
        cmp     dword [rbp-18H], eax
        jl      L_035
        mov     edx, dword [rbp-14H]
        mov     eax, dword [rbp-10H]
        add     eax, edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     byte [rax], 0
        mov     eax, 8
        mov     eax, eax
        neg     rax
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        leave
        ret


str_equal:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        add     rdx, rax
        mov     eax, 8
        mov     ecx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rcx
        mov     rsi, rdx
        mov     rdi, rax
        call    strcmp
        test    eax, eax
        sete    al
        movzx   eax, al
        leave
        ret


str_not_equal:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        add     rdx, rax
        mov     eax, 8
        mov     ecx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rcx
        mov     rsi, rdx
        mov     rdi, rax
        call    strcmp
        test    eax, eax
        setne   al
        movzx   eax, al
        leave
        ret


str_less:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        add     rdx, rax
        mov     eax, 8
        mov     ecx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rcx
        mov     rsi, rdx
        mov     rdi, rax
        call    strcmp
        shr     eax, 31
        leave
        ret


str_lte:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     qword [rbp-8H], rdi
        mov     qword [rbp-10H], rsi
        mov     eax, 8
        mov     edx, eax
        mov     rax, qword [rbp-10H]
        add     rdx, rax
        mov     eax, 8
        mov     ecx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rcx
        mov     rsi, rdx
        mov     rdi, rax
        call    strcmp
        test    eax, eax
        setle   al
        movzx   eax, al
        leave
        ret


parseInt:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-18H], rdi
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-18H], rax
        mov     dword [rbp-8H], 0
        mov     eax, dword [rbp-8H]
        lea     edx, [rax+1H]
        mov     dword [rbp-8H], edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-18H]
        add     rax, rdx
        movzx   eax, byte [rax]
        mov     byte [rbp-0DH], al
        jmp     L_040

L_039:  mov     eax, dword [rbp-8H]
        lea     edx, [rax+1H]
        mov     dword [rbp-8H], edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-18H]
        add     rax, rdx
        movzx   eax, byte [rax]
        mov     byte [rbp-0DH], al
L_040:  cmp     byte [rbp-0DH], 45
        jz      L_041
        cmp     byte [rbp-0DH], 47
        jle     L_039
        cmp     byte [rbp-0DH], 57
        jg      L_039
L_041:  cmp     byte [rbp-0DH], 45
        jnz     L_042
        mov     eax, 4294967295
        jmp     L_043

L_042:  mov     eax, 1
L_043:  mov     dword [rbp-4H], eax
        cmp     byte [rbp-0DH], 45
        jz      L_044
        movsx   eax, byte [rbp-0DH]
        sub     eax, 48
        jmp     L_045

L_044:  mov     eax, 0
L_045:  mov     dword [rbp-0CH], eax
        jmp     L_047

L_046:  mov     edx, dword [rbp-0CH]
        mov     eax, edx
        shl     eax, 2
        add     eax, edx
        add     eax, eax
        mov     edx, eax
        movsx   eax, byte [rbp-0DH]
        add     eax, edx
        sub     eax, 48
        mov     dword [rbp-0CH], eax
L_047:  mov     eax, dword [rbp-8H]
        lea     edx, [rax+1H]
        mov     dword [rbp-8H], edx
        movsxd  rdx, eax
        mov     rax, qword [rbp-18H]
        add     rax, rdx
        movzx   eax, byte [rax]
        mov     byte [rbp-0DH], al
        cmp     byte [rbp-0DH], 47
        jle     L_048
        cmp     byte [rbp-0DH], 57
        jg      L_048
        mov     eax, 1
        jmp     L_049

L_048:  mov     eax, 0
L_049:  test    eax, eax
        jnz     L_046
        cmp     dword [rbp-4H], 0
        jg      L_050
        mov     eax, dword [rbp-0CH]
        neg     eax
        jmp     L_051

L_050:  mov     eax, dword [rbp-0CH]
L_051:  pop     rbp
        ret


ord:
        push    rbp
        mov     rbp, rsp
        mov     qword [rbp-8H], rdi
        mov     dword [rbp-0CH], esi
        mov     eax, dword [rbp-0CH]
        mov     edx, 8
        add     eax, edx
        mov     edx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        movzx   eax, byte [rax]
        movsx   eax, al
        pop     rbp
        ret


substring:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 32
        mov     qword [rbp-18H], rdi
        mov     dword [rbp-1CH], esi
        mov     dword [rbp-20H], edx
        mov     eax, dword [rbp-20H]
        sub     eax, dword [rbp-1CH]
        add     eax, 1
        mov     dword [rbp-0CH], eax
        mov     eax, dword [rbp-0CH]
        mov     edx, 8
        add     eax, edx
        add     eax, 1
        mov     eax, eax
        mov     rdi, rax
        call    malloc
        mov     qword [rbp-8H], rax
        mov     eax, dword [rbp-0CH]
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        mov     qword [rax], rdx
        mov     eax, dword [rbp-1CH]
        mov     edx, 8
        add     eax, edx
        mov     eax, eax
        add     qword [rbp-18H], rax
        mov     eax, 8
        mov     eax, eax
        add     qword [rbp-8H], rax
        mov     dword [rbp-10H], 0
        jmp     L_053

L_052:  mov     eax, dword [rbp-10H]
        movsxd  rdx, eax
        mov     rax, qword [rbp-18H]
        add     rax, rdx
        mov     edx, dword [rbp-10H]
        movsxd  rcx, edx
        mov     rdx, qword [rbp-8H]
        add     rdx, rcx
        movzx   eax, byte [rax]
        mov     byte [rdx], al
        add     dword [rbp-10H], 1
L_053:  mov     eax, dword [rbp-10H]
        cmp     eax, dword [rbp-0CH]
        jl      L_052
        mov     eax, dword [rbp-0CH]
        movsxd  rdx, eax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        mov     byte [rax], 0
        mov     eax, 8
        mov     eax, eax
        neg     rax
        mov     rdx, rax
        mov     rax, qword [rbp-8H]
        add     rax, rdx
        leave
        ret



SECTION .data   


SECTION .bss    


SECTION .rodata align=4

REG_SIZE:
        dd 00000008H

L_054:
        db 25H, 73H, 00H


