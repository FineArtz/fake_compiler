global __print
global __println
global __printInt
global __printlnInt
global __getString
global __getInt
global __toString
global __str_concat
global __str_equal
global __str_not_equal
global __str_less
global __str_lte
global __parseInt
global __ord
global __substring
global REG_SIZE

extern strcmp
extern memcpy
extern _IO_getc
extern stdin
extern __isoc99_scanf
extern malloc
extern __stack_chk_fail
extern _IO_putc
extern stdout
extern puts
extern __printf_chk
extern _GLOBAL_OFFSET_TABLE_


SECTION .text   6

__print:
        lea     rdx, [rdi+8H]
        lea     rsi, [rel .LC0]
        mov     edi, 1
        xor     eax, eax
        jmp     __printf_chk







ALIGN   16

__println:
        add     rdi, 8
        jmp     puts






ALIGN   8

__printInt:
        push    rbp
        push    rbx
        sub     rsp, 56
        mov     rsi, qword [rel stdout]


        mov     rax, qword [fs:abs 28H]
        mov     qword [rsp+28H], rax
        xor     eax, eax
        test    edi, edi
        je      L_005
        mov     ebx, edi
        js      L_007
L_001:  mov     eax, ebx
        mov     edi, 3435973837
        mul     edi
        mov     ecx, edx
        mov     edx, ebx
        shr     ecx, 3
        lea     eax, [rcx+rcx*4]
        add     eax, eax
        sub     edx, eax
        test    ecx, ecx
        mov     dword [rsp], edx
        je      L_002
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1374389535
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+4H], ecx
        mov     ecx, edx
        shr     ecx, 5
        test    ecx, ecx
        je      L_006
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 274877907
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+8H], ecx
        mov     ecx, edx
        shr     ecx, 6
        test    ecx, ecx
        je      L_008
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 3518437209
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+0CH], ecx
        shr     edx, 13
        test    edx, edx
        mov     ecx, edx
        je      L_009
        mov     eax, edx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, ebx
        shr     edx, 5
        add     eax, eax
        sub     ecx, eax
        mov     eax, edx
        mov     dword [rsp+10H], ecx
        mov     ecx, 175921861
        mul     ecx
        mov     ecx, edx
        shr     ecx, 7
        test    ecx, ecx
        je      L_010
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1125899907
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+14H], ecx
        shr     edx, 18
        test    edx, edx
        mov     ecx, edx
        je      L_011
        mov     eax, edx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1801439851
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+18H], ecx
        mov     ecx, edx
        shr     ecx, 22
        test    ecx, ecx
        je      L_012
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1441151881
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+1CH], ecx
        mov     ecx, edx
        shr     ecx, 25
        test    ecx, ecx
        je      L_013
        mov     eax, ecx
        shr     ebx, 9
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 281475
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+20H], ecx
        shr     edx, 7
        test    edx, edx
        je      L_014
        mov     dword [rsp+24H], edx
        mov     ecx, 9
L_002:  shl     rcx, 2
        lea     rbp, [rsp-4H]
        lea     rbx, [rsp+rcx]
        jmp     L_004





ALIGN   8
L_003:  mov     rsi, qword [rel stdout]
L_004:  mov     eax, dword [rbx]
        sub     rbx, 4
        lea     edi, [rax+30H]
        call    _IO_putc
        cmp     rbx, rbp
        jnz     L_003
        mov     rax, qword [rsp+28H]


        xor     rax, qword [fs:abs 28H]
        jne     L_015
        add     rsp, 56
        pop     rbx
        pop     rbp
        ret





ALIGN   8
L_005:  mov     rax, qword [rsp+28H]


        xor     rax, qword [fs:abs 28H]
        jnz     L_015
        add     rsp, 56
        mov     edi, 48
        pop     rbx
        pop     rbp
        jmp     _IO_putc

L_006:  mov     ecx, 1
        jmp     L_002





ALIGN   8
L_007:  mov     edi, 45
        neg     ebx
        call    _IO_putc
        mov     rsi, qword [rel stdout]
        jmp     L_001

L_008:  mov     ecx, 2
        jmp     L_002

L_009:  mov     ecx, 3
        jmp     L_002

L_010:  mov     ecx, 4
        jmp     L_002

L_011:  mov     ecx, 5
        jmp     L_002

L_012:  mov     ecx, 6
        jmp     L_002

L_013:  mov     ecx, 7
        jmp     L_002

L_014:  mov     ecx, 8
        jmp     L_002


L_015:
        call    __stack_chk_fail





ALIGN   16

__printlnInt:
        push    rbp
        push    rbx
        sub     rsp, 56
        mov     rsi, qword [rel stdout]


        mov     rax, qword [fs:abs 28H]
        mov     qword [rsp+28H], rax
        xor     eax, eax
        test    edi, edi
        je      L_021
        mov     ebx, edi
        js      L_023
L_016:  mov     eax, ebx
        mov     edi, 3435973837
        mul     edi
        mov     ecx, edx
        mov     edx, ebx
        shr     ecx, 3
        lea     eax, [rcx+rcx*4]
        add     eax, eax
        sub     edx, eax
        test    ecx, ecx
        mov     dword [rsp], edx
        je      L_017
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1374389535
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+4H], ecx
        mov     ecx, edx
        shr     ecx, 5
        test    ecx, ecx
        je      L_022
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 274877907
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+8H], ecx
        mov     ecx, edx
        shr     ecx, 6
        test    ecx, ecx
        je      L_024
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 3518437209
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+0CH], ecx
        shr     edx, 13
        test    edx, edx
        mov     ecx, edx
        je      L_025
        mov     eax, edx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, ebx
        shr     edx, 5
        add     eax, eax
        sub     ecx, eax
        mov     eax, edx
        mov     dword [rsp+10H], ecx
        mov     ecx, 175921861
        mul     ecx
        mov     ecx, edx
        shr     ecx, 7
        test    ecx, ecx
        je      L_026
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1125899907
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+14H], ecx
        shr     edx, 18
        test    edx, edx
        mov     ecx, edx
        je      L_027
        mov     eax, edx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1801439851
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+18H], ecx
        mov     ecx, edx
        shr     ecx, 22
        test    ecx, ecx
        je      L_028
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1441151881
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+1CH], ecx
        mov     ecx, edx
        shr     ecx, 25
        test    ecx, ecx
        je      L_029
        mov     eax, ecx
        shr     ebx, 9
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 281475
        add     eax, eax
        sub     ecx, eax
        mov     eax, ebx
        mul     edx
        mov     dword [rsp+20H], ecx
        shr     edx, 7
        test    edx, edx
        je      L_030
        mov     dword [rsp+24H], edx
        mov     ecx, 9
L_017:  shl     rcx, 2
        lea     rbp, [rsp-4H]
        lea     rbx, [rsp+rcx]
        jmp     L_019





ALIGN   8
L_018:  mov     rsi, qword [rel stdout]
L_019:  mov     eax, dword [rbx]
        sub     rbx, 4
        lea     edi, [rax+30H]
        call    _IO_putc
        cmp     rbx, rbp
        jnz     L_018
L_020:  mov     rax, qword [rsp+28H]


        xor     rax, qword [fs:abs 28H]
        jne     L_031
        mov     rsi, qword [rel stdout]
        add     rsp, 56
        mov     edi, 10
        pop     rbx
        pop     rbp
        jmp     _IO_putc





ALIGN   8
L_021:  mov     edi, 48
        call    _IO_putc
        jmp     L_020

L_022:  mov     ecx, 1
        jmp     L_017





ALIGN   8
L_023:  mov     edi, 45
        neg     ebx
        call    _IO_putc
        mov     rsi, qword [rel stdout]
        jmp     L_016

L_024:  mov     ecx, 2
        jmp     L_017

L_025:  mov     ecx, 3
        jmp     L_017

L_026:  mov     ecx, 4
        jmp     L_017

L_027:  mov     ecx, 5
        jmp     L_017

L_028:  mov     ecx, 6
        jmp     L_017

L_029:  mov     ecx, 7
        jmp     L_017

L_030:  mov     ecx, 8
        jmp     L_017

L_031:
        call    __stack_chk_fail





ALIGN   16

__getString:
        push    rbp
        push    rbx
        mov     edi, 266
        sub     rsp, 8
        call    malloc
        lea     rbx, [rax+8H]
        lea     rdi, [rel .LC0]
        mov     rbp, rax
        xor     eax, eax
        mov     rsi, rbx
        call    __isoc99_scanf
        mov     rdx, rbx
L_032:  mov     ecx, dword [rdx]
        add     rdx, 4
        lea     eax, [rcx-1010101H]
        not     ecx
        and     eax, ecx
        and     eax, 80808080H
        jz      L_032
        mov     ecx, eax
        shr     ecx, 16
        test    eax, 8080H
        cmove   eax, ecx
        lea     rcx, [rdx+2H]
        mov     esi, eax
        cmove   rdx, rcx
        add     sil, al
        mov     rax, rbp
        sbb     rdx, 3
        sub     rdx, rbx
        mov     qword [rbp], rdx
        add     rsp, 8
        pop     rbx
        pop     rbp
        ret






ALIGN   16

__getInt:
        push    rbp
        push    rbx
        sub     rsp, 8
        jmp     L_034





ALIGN   16
L_033:  cmp     al, 45
        jz      L_035
L_034:  mov     rdi, qword [rel stdin]
        call    _IO_getc
        lea     edx, [rax-30H]
        movsx   ebx, al
        cmp     dl, 9
        ja      L_033
L_035:  cmp     bl, 45
        jz      L_038
        sub     ebx, 48
        mov     ebp, 1
        jmp     L_037





ALIGN   8
L_036:  lea     edx, [rbx+rbx*4]
        movsx   eax, al
        lea     ebx, [rax+rdx*2-30H]
L_037:  mov     rdi, qword [rel stdin]
        call    _IO_getc
        lea     edx, [rax-30H]
        cmp     dl, 9
        jbe     L_036
        mov     eax, ebx
        add     rsp, 8
        imul    eax, ebp
        pop     rbx
        pop     rbp
        ret





ALIGN   8
L_038:  mov     ebp, 4294967295
        xor     ebx, ebx
        jmp     L_037






ALIGN   8

__toString:
        push    r12
        push    rbp
        mov     ebp, edi
        push    rbx
        sub     rsp, 48


        mov     rax, qword [fs:abs 28H]
        mov     qword [rsp+28H], rax
        xor     eax, eax
        test    edi, edi
        js      L_041
        jne     L_044
        mov     edi, 10
        mov     dword [rsp], 0
        mov     ebx, 1
        call    malloc
        lea     rdx, [rax+8H]
        mov     qword [rax], 1
        mov     ecx, 1
L_039:  lea     esi, [rbx-1H]
        movsxd  rdi, ebp
        movsxd  rsi, esi
        mov     esi, dword [rsp+rsi*4]
        add     esi, 48
        cmp     ebx, 1
        mov     byte [rdx+rdi], sil
        je      L_040
        lea     edi, [rbx-2H]
        lea     esi, [rbp+1H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 2
        mov     byte [rdx+rsi], dil
        je      L_040
        lea     edi, [rbx-3H]
        lea     esi, [rbp+2H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 3
        mov     byte [rdx+rsi], dil
        je      L_040
        lea     edi, [rbx-4H]
        lea     esi, [rbp+3H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 4
        mov     byte [rdx+rsi], dil
        je      L_040
        lea     edi, [rbx-5H]
        lea     esi, [rbp+4H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 5
        mov     byte [rdx+rsi], dil
        jz      L_040
        lea     edi, [rbx-6H]
        lea     esi, [rbp+5H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 6
        mov     byte [rdx+rsi], dil
        jz      L_040
        lea     edi, [rbx-7H]
        lea     esi, [rbp+6H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 7
        mov     byte [rdx+rsi], dil
        jz      L_040
        lea     edi, [rbx-8H]
        lea     esi, [rbp+7H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 8
        mov     byte [rdx+rsi], dil
        jz      L_040
        lea     edi, [rbx-9H]
        lea     esi, [rbp+8H]
        movsxd  rdi, edi
        movsxd  rsi, esi
        mov     edi, dword [rsp+rdi*4]
        add     edi, 48
        cmp     ebx, 10
        mov     byte [rdx+rsi], dil
        jnz     L_040
        movzx   ebx, byte [rsp]
        add     ebp, 9
        movsxd  rbp, ebp
        lea     esi, [rbx+30H]
        mov     byte [rdx+rbp], sil
L_040:  mov     rbx, qword [rsp+28H]


        xor     rbx, qword [fs:abs 28H]
        mov     byte [rdx+rcx], 0
        jne     L_053
        add     rsp, 48
        pop     rbx
        pop     rbp
        pop     r12
        ret

L_041:  mov     esi, edi
        mov     ebp, 1
        neg     esi
L_042:  mov     eax, esi
        mov     edi, 3435973837
        mov     ebx, esi
        mul     edi
        mov     ecx, edx
        shr     ecx, 3
        lea     eax, [rcx+rcx*4]
        add     eax, eax
        sub     ebx, eax
        test    ecx, ecx
        mov     dword [rsp], ebx
        je      L_045
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1374389535
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+4H], ecx
        mov     ecx, edx
        shr     ecx, 5
        test    ecx, ecx
        je      L_046
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 274877907
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+8H], ecx
        mov     ecx, edx
        shr     ecx, 6
        test    ecx, ecx
        je      L_047
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 3518437209
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+0CH], ecx
        mov     ecx, edx
        shr     ecx, 13
        test    ecx, ecx
        je      L_049
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, esi
        shr     edx, 5
        add     eax, eax
        sub     ecx, eax
        mov     eax, edx
        mov     dword [rsp+10H], ecx
        mov     ecx, 175921861
        mul     ecx
        mov     ecx, edx
        shr     ecx, 7
        test    ecx, ecx
        je      L_048
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1125899907
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+14H], ecx
        mov     ecx, edx
        shr     ecx, 18
        test    ecx, ecx
        je      L_050
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1801439851
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+18H], ecx
        mov     ecx, edx
        shr     ecx, 22
        test    ecx, ecx
        je      L_051
        mov     eax, ecx
        mul     edi
        shr     edx, 3
        lea     eax, [rdx+rdx*4]
        mov     edx, 1441151881
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+1CH], ecx
        mov     ecx, edx
        shr     ecx, 25
        test    ecx, ecx
        je      L_052
        mov     eax, ecx
        shr     esi, 9
        mov     ebx, 9
        mul     edi
        mov     edi, edx
        mov     edx, 281475
        shr     edi, 3
        lea     eax, [rdi+rdi*4]
        add     eax, eax
        sub     ecx, eax
        mov     eax, esi
        mul     edx
        mov     dword [rsp+20H], ecx
        shr     edx, 7
        test    edx, edx
        jz      L_043
        mov     dword [rsp+24H], edx
        mov     ebx, 10




ALIGN   8
L_043:  lea     r12d, [rbx+rbp]
        lea     edi, [r12+9H]
        call    malloc
        movsxd  rcx, r12d
        test    ebp, ebp
        lea     rdx, [rax+8H]
        mov     qword [rax], rcx
        je      L_039
        mov     byte [rax+8H], 45
        jmp     L_039





ALIGN   8
L_044:  mov     esi, edi
        xor     ebp, ebp
        jmp     L_042





ALIGN   8
L_045:  mov     ebx, 1
        jmp     L_043






ALIGN   16
L_046:  mov     ebx, 2
        jmp     L_043






ALIGN   16
L_047:  mov     ebx, 3
        jmp     L_043






ALIGN   16
L_048:  mov     ebx, 5
        jmp     L_043






ALIGN   16
L_049:  mov     ebx, 4
        jmp     L_043





ALIGN   8
L_050:  mov     ebx, 6
        jmp     L_043





ALIGN   8
L_051:  mov     ebx, 7
        jmp     L_043





ALIGN   8
L_052:  mov     ebx, 8
        jmp     L_043


L_053:
        call    __stack_chk_fail
        nop
ALIGN   16

__str_concat:
        push    r15
        push    r14
        mov     r15, rsi
        push    r13
        push    r12
        push    rbp
        push    rbx
        mov     rbp, rdi
        sub     rsp, 24
        mov     r14, qword [rdi]
        mov     r12d, dword [rsi]
        add     r12d, r14d
        lea     edi, [r12+9H]
        movsxd  r13, r12d
        call    malloc
        test    r12d, r12d
        mov     rbx, rax
        mov     qword [rax], r13
        jle     L_055
        test    r14d, r14d
        jg      L_056
        xor     ecx, ecx
L_054:  lea     edx, [r12-1H]
        movsxd  rax, ecx
        movsxd  r14, r14d
        lea     rdi, [rbx+rax+8H]
        sub     edx, ecx
        add     rdx, 1
        cmp     r12d, ecx
        mov     ecx, 1
        cmovle  rdx, rcx
        sub     rax, r14
        lea     rsi, [r15+rax+8H]
        call    memcpy
L_055:  mov     byte [rbx+r13+8H], 0
        add     rsp, 24
        mov     rax, rbx
        pop     rbx
        pop     rbp
        pop     r12
        pop     r13
        pop     r14
        pop     r15
        ret

L_056:
        cmp     r12d, r14d
        mov     esi, r14d
        lea     rax, [rbp+8H]
        cmovle  esi, r12d
        mov     edi, 1
        mov     edx, 0
        test    esi, esi
        lea     ecx, [rsi-1H]
        mov     r8d, 16
        cmovg   edi, esi
        neg     rax
        and     eax, 0FH
        test    esi, esi
        cmovle  ecx, edx
        lea     edx, [rax+0FH]
        cmp     edx, 16
        cmovc   edx, r8d
        cmp     ecx, edx
        jc      L_065
        test    eax, eax
        je      L_070
        movzx   edx, byte [rbp+8H]
        cmp     eax, 1
        mov     byte [rbx+8H], dl
        je      L_063
        movzx   edx, byte [rbp+9H]
        cmp     eax, 2
        mov     byte [rbx+9H], dl
        je      L_064
        movzx   edx, byte [rbp+0AH]
        cmp     eax, 3
        mov     byte [rbx+0AH], dl
        je      L_062
        movzx   edx, byte [rbp+0BH]
        cmp     eax, 4
        mov     byte [rbx+0BH], dl
        je      L_066
        movzx   edx, byte [rbp+0CH]
        cmp     eax, 5
        mov     byte [rbx+0CH], dl
        je      L_067
        movzx   edx, byte [rbp+0DH]
        cmp     eax, 6
        mov     byte [rbx+0DH], dl
        je      L_069
        movzx   edx, byte [rbp+0EH]
        cmp     eax, 7
        mov     byte [rbx+0EH], dl
        je      L_071
        movzx   edx, byte [rbp+0FH]
        cmp     eax, 8
        mov     byte [rbx+0FH], dl
        je      L_068
        movzx   edx, byte [rbp+10H]
        cmp     eax, 9
        mov     byte [rbx+10H], dl
        je      L_072
        movzx   edx, byte [rbp+11H]
        cmp     eax, 10
        mov     byte [rbx+11H], dl
        je      L_073
        movzx   edx, byte [rbp+12H]
        cmp     eax, 11
        mov     byte [rbx+12H], dl
        je      L_074
        movzx   edx, byte [rbp+13H]
        cmp     eax, 12
        mov     byte [rbx+13H], dl
        je      L_075
        movzx   edx, byte [rbp+14H]
        cmp     eax, 13
        mov     byte [rbx+14H], dl
        je      L_076
        movzx   edx, byte [rbp+15H]
        cmp     eax, 15
        mov     byte [rbx+15H], dl
        jne     L_077
        movzx   edx, byte [rbp+16H]
        mov     dword [rsp+0CH], 15
        mov     byte [rbx+16H], dl




ALIGN   8
L_057:  movd    xmm4, dword [rsp+0CH]
        sub     edi, eax
        add     rax, 8
        mov     r8d, edi
        movdqa  xmm3, oword [rel .LC2]
        pshufd  xmm0, xmm4, 00H
        lea     r9, [rbp+rax]
        movdqa  xmm2, oword [rel .LC3]
        shr     r8d, 4
        add     rax, rbx
        xor     edx, edx
        xor     ecx, ecx
        paddd   xmm0, oword [rel .LC1]




ALIGN   8
L_058:  movdqa  xmm1, oword [r9+rdx]
        add     ecx, 1
        movups  oword [rax+rdx], xmm1
        movdqa  xmm1, xmm0
        add     rdx, 16
        paddd   xmm0, xmm3
        cmp     r8d, ecx
        paddd   xmm1, xmm2
        ja      L_058
        mov     eax, dword [rsp+0CH]
        pshufd  xmm1, xmm1, 0FFH
        mov     edx, edi
        and     edx, 0FFFFFFF0H
        add     eax, edx
        cmp     edi, edx
        movd    ecx, xmm1
        jz      L_061
L_059:  cdqe




ALIGN   8
L_060:  movzx   edx, byte [rbp+rax+8H]
        lea     ecx, [rax+1H]
        mov     byte [rbx+rax+8H], dl
        add     rax, 1
        cmp     esi, eax
        jg      L_060
L_061:  cmp     r12d, ecx
        jg      L_054
        jmp     L_055





ALIGN   8
L_062:  mov     dword [rsp+0CH], 3
        jmp     L_057





ALIGN   8
L_063:  mov     dword [rsp+0CH], 1
        jmp     L_057





ALIGN   8
L_064:  mov     dword [rsp+0CH], 2
        jmp     L_057





ALIGN   8
L_065:  xor     eax, eax
        jmp     L_059





ALIGN   8
L_066:  mov     dword [rsp+0CH], 4
        jmp     L_057





ALIGN   8
L_067:  mov     dword [rsp+0CH], 5
        jmp     L_057

L_068:  mov     dword [rsp+0CH], 8
        jmp     L_057

L_069:  mov     dword [rsp+0CH], 6
        jmp     L_057

L_070:  mov     dword [rsp+0CH], 0
        jmp     L_057

L_071:  mov     dword [rsp+0CH], 7
        jmp     L_057

L_072:  mov     dword [rsp+0CH], 9
        jmp     L_057

L_073:  mov     dword [rsp+0CH], 10
        jmp     L_057

L_074:  mov     dword [rsp+0CH], 11
        jmp     L_057

L_075:  mov     dword [rsp+0CH], 12
        jmp     L_057

L_076:  mov     dword [rsp+0CH], 13
        jmp     L_057

L_077:
        mov     dword [rsp+0CH], 14
        jmp     L_057

        nop

ALIGN   16
__str_equal:
        sub     rsp, 8
        add     rsi, 8
        add     rdi, 8
        call    strcmp
        test    eax, eax
        sete    al
        add     rsp, 8
        movzx   eax, al
        ret






ALIGN   8

__str_not_equal:
        sub     rsp, 8
        add     rsi, 8
        add     rdi, 8
        call    strcmp
        test    eax, eax
        setne   al
        add     rsp, 8
        movzx   eax, al
        ret






ALIGN   8

__str_less:
        sub     rsp, 8
        add     rsi, 8
        add     rdi, 8
        call    strcmp
        add     rsp, 8
        shr     eax, 31
        ret






ALIGN   8

__str_lte:
        sub     rsp, 8
        add     rsi, 8
        add     rdi, 8
        call    strcmp
        test    eax, eax
        setle   al
        add     rsp, 8
        movzx   eax, al
        ret






ALIGN   8

__parseInt:
        movsx   eax, byte [rdi+8H]
        lea     edx, [rax-30H]
        cmp     dl, 9
        jbe     L_088
        cmp     al, 45
        je      L_088
        mov     edx, 2
        jmp     L_079





ALIGN   8
L_078:  cmp     al, 45
        jz      L_080
L_079:  movsx   eax, byte [rdi+rdx+7H]
        mov     esi, edx
        add     rdx, 1
        lea     ecx, [rax-30H]
        cmp     cl, 9
        ja      L_078
L_080:  movsxd  rdx, esi
        add     esi, 1
L_081:  cmp     al, 45
        jz      L_087
        movsx   edx, byte [rdi+rdx+8H]
        sub     eax, 48
        mov     r8d, 1
        lea     ecx, [rdx-30H]
        cmp     cl, 9
        ja      L_084
L_082:  movsxd  rsi, esi
        lea     rcx, [rdi+rsi+8H]
        nop
L_083:  lea     eax, [rax+rax*4]
        add     rcx, 1
        lea     eax, [rdx+rax*2-30H]
        movsx   edx, byte [rcx-1H]
        lea     esi, [rdx-30H]
        cmp     sil, 9
        jbe     L_083
        cmp     r8d, 1
        jnz     L_086
L_084:  DB      0F3H
        ret

L_085:  xor     eax, eax
L_086:  neg     eax
        ret





ALIGN   8
L_087:  movsx   edx, byte [rdi+rdx+8H]
        lea     eax, [rdx-30H]
        cmp     al, 9
        ja      L_085
        mov     r8d, 4294967295
        xor     eax, eax
        jmp     L_082





ALIGN   8
L_088:  mov     edx, 1
        mov     esi, 2
        jmp     L_081






ALIGN   8

__ord:
        lea     eax, [rsi+8H]
        movsx   eax, byte [rdi+rax]
        ret






ALIGN   16

__substring:
        push    r15
        push    r14
        mov     r15d, esi
        push    r13
        push    r12
        mov     r12, rdi
        push    rbp
        push    rbx
        mov     ebx, edx
        sub     ebx, esi
        lea     r14d, [rbx+1H]
        lea     edi, [rbx+0AH]
        sub     rsp, 8
        call    malloc
        movsxd  r13, r14d
        test    r14d, r14d
        mov     rbp, rax
        mov     qword [rax], r13
        jle     L_089
        lea     esi, [r15+8H]
        mov     edx, ebx
        lea     rdi, [rax+8H]
        add     rdx, 1
        add     rsi, r12
        call    memcpy
L_089:  mov     byte [rbp+r13+8H], 0
        add     rsp, 8
        mov     rax, rbp
        pop     rbx
        pop     rbp
        pop     r12
        pop     r13
        pop     r14
        pop     r15
        ret



SECTION .data   


SECTION .bss    


SECTION .rodata.str1.1 

.LC0:
        db 25H, 73H, 00H


SECTION .text.startup 6



SECTION .rodata align=4

REG_SIZE:
        dd 00000008H


SECTION .rodata.cst16 6

ALIGN   16
.LC1:
        dd 00000000H, 00000001H
        dd 00000002H, 00000003H

.LC2:
        dq 0000001000000010H
        dq 0000001000000010H

.LC3:
        dq 0000000D0000000DH
        dq 0000000D0000000DH


