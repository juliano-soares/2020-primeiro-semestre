int main ( ) {
    int a , b ;
    float c ;
    a = 1 ;
    b = 12 ;
    char d = 'F' ;

    if ( a > b ) {
        printf ( "é maior" ) ; 
    } ;
    
    int i = 0 ;

    while ( i < b ) {
        printf ( "oi" ) ;
        i = i + 1 ;
    } ;

    for ( i = 1 ; i != b ; i = i + 1 ) {
        printf ( "%d \n " , i ) ;
    };

    c = ( a + b ) - ( c * b ) / [ a * b ] ;

    printf ( "o resultado é: %d" , c ) ;
}



