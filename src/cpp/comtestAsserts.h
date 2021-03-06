#include <iostream>
#include <cstring>
namespace comtestCppNameSpace {
//#define assertEquals(s,e,g) { if ( comtestCppNameSpace::assertEqualsImpl((s),(e),(g)) ) return; }
//#define assertEqualsDelta(s,e,g,delta) { if ( comtestCppNameSpace::assertEqualsImpl((s),(e),(g),(delta)) ) return; }
#define assertEquals comtestCppNameSpace::assertEqualsImpl

template <class T1>
class assertion_traits {
  public: static bool equal( const T1& x, const T1& y ) {
    return x == y;
  }
};


template<>
class assertion_traits<const char *> {
  public: static bool equal( const char *x,const char *y) {
    if ( x == 0 && y == 0 ) return true;
    if ( x == 0 || y == 0 ) return false;
    return std::strcmp(x,y) == 0;
  }
};

template<>
class assertion_traits<char *> {
  public: static bool equal( const char *x,char *y) {
    if ( x == 0 && y == 0 ) return true;
    if ( x == 0 || y == 0 ) return false;
    return std::strcmp(x,y) == 0;
  }
};

template <class T1>
int assertEqualsImpl(const char *msg,const T1 &expected, const T1 &actual) {
if ( assertion_traits<T1>::equal(expected,actual) ) return 0;
std::cout << msg << ": expected [" << expected << "] actual [" << actual << "]" << std::endl;
throw int(1);
}

template <class T1>
int assertEqualsImpl(const char *msg,const char  *expected, const T1 &actual) {
if ( assertion_traits<T1>::equal(expected,actual) ) return 0;
if ( expected == 0 ) std::cout << msg << ": expected [null] actual [" << actual << "]" << std::endl;
else if ( &actual == 0 ) std::cout << msg << ": expected [" << expected << "] actual [null]" << std::endl;
else std::cout << msg << ": expected [" << expected << "] actual [" << actual << "]" << std::endl;
throw int(1);
}

int assertEqualsImpl(const char *msg,int expected, int actual) {
if ( expected == actual ) return 0;
std::cout << msg << ": expected [" << expected << "] actual [" << actual << "]" << std::endl;
throw int(1);
}

int assertEqualsImpl(const char *msg,long expected, long actual) {
if ( expected == actual ) return 0;
std::cout << msg << ": expected [" << expected << "] actual [" << actual << "]" << std::endl;
throw int(1);
}


int assertEqualsImpl(const char *msg,double expected, double actual, double delta) {
if ( -delta <= (actual-expected) && (actual-expected) <= delta ) return 0;
std::cout << msg << ": expected [" << expected << "] actual [" << actual << "]" << std::endl;
throw int(1);
}

}
