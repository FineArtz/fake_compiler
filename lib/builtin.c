// 2109-05-03

#include <stdio.h>
#include <string.h>
#include <malloc.h>

const unsigned int REG_SIZE=8;

void print(char *s)
{
    printf("%s",s+REG_SIZE);
}

void println(char *s)
{
    puts(s+REG_SIZE);
}

void printInt(int x)
{
    if (x==0) putchar('0');
    else if (x<0) x=-x, putchar('-');
    int a[10];
    int l=0;
    for (;x>0;x/=10) a[l++]=x%10;
    int i;
    for (i=l-1;i>=0;--i) putchar(a[i]+'0');
}

void printlnInt(int x
){
    if (x==0) putchar('0');
    else if (x<0) x=-x, putchar('-');
    int a[10];
    int l=0;
    for (;x>0;x/=10) a[l++]=x%10;
    int i;
    for (i=l-1;i>=0;--i) putchar(a[i]+'0');
    putchar('\n');
}

char *getString()
{
    char *s=(char*)malloc(266);
    scanf("%s",s+REG_SIZE);
    *((long*)s)=strlen(s+REG_SIZE);
    return s;
}

int getInt()
{
    char c;
    int sgn;
    int x;
    c=getchar();
    while (c!='-'&&(c<'0'||c>'9')) c=getchar();
    sgn=(c=='-')?-1:1;
    x=(c=='-')?0:(c-'0');
    while (c=getchar(),c>='0'&&c<='9') x=x*10+c-'0';
    x*=sgn;
    return x;
}

char *toString(int x)
{
    int sgn;
    int l;
    if (x<0) x=-x,sgn=-1;
    else sgn=0;
    int a[10];
    l=0;
    if (x==0) a[l++]=0;
    else for(;x>0;x/=10) a[l++]=x%10;
    char *s=(char*)malloc(sgn+l+REG_SIZE+1);
    *((long*)s)=sgn+l;
    s+=REG_SIZE;
    if (sgn) s[0]='-';
    int i;
    for (i=0;i<l;++i) s[sgn+i]=a[l-i-1]+'0';
    s[sgn+l]='\0';
    return s-REG_SIZE;
}

char *str_concat(char *s1,char *s2)
{
    int l1=*((long*)s1),l2=*((long*)s2);
    char *s=(char*)malloc(l1+l2+REG_SIZE+1);
    *((long*)s)=l1+l2;
    s1+=REG_SIZE;
    s2+=REG_SIZE;
    s+=REG_SIZE;
    int l=0;
    int i;
    for (i=0;i<l1+l2;++i) s[i]=(i<l1)?s1[i]:s2[i-l1];
    s[l1+l2]='\0';
    return s-REG_SIZE;
}

int str_equal(char *s1,char *s2)
{
    return (strcmp(s1+REG_SIZE,s2+REG_SIZE)==0?1:0);
}

int str_not_equal(char *s1,char *s2)
{
    return (strcmp(s1+REG_SIZE,s2+REG_SIZE)!=0?1:0);
}

int str_less(char *s1,char *s2)
{
    return (strcmp(s1+REG_SIZE,s2+REG_SIZE)<0?1:0);
}

int str_lte(char *s1,char *s2)
{
    return (strcmp(s1+REG_SIZE,s2+REG_SIZE)<=0?1:0);
}

int parseInt(char *s)
{
    s+=REG_SIZE;
    char c;
    int sgn;
    int x;
    int i=0;
    c=s[i++];
    while (c!='-'&&(c<'0'||c>'9')) c=s[i++];
    sgn=(c=='-')?-1:1;
    x=(c=='-')?0:(c-'0');
    while (c=s[i++],c>='0'&&c<='9') x=x*10+c-'0';
    return (sgn>0)?x:-x;
}

int ord(char *s,int n)
{
    return s[n+REG_SIZE];
}

char *substring(char *s,int x,int y)
{
    int l=y-x+1;
    char *ss=(char*)malloc(l+REG_SIZE+1);
    *((long*)ss)=l;
    s+=x+REG_SIZE;
    ss+=REG_SIZE;
    int i;
    for (i=0;i<l;++i) ss[i]=s[i];
    ss[l]='\0';
    return ss-REG_SIZE;
}

int main(){
    return 0;
}