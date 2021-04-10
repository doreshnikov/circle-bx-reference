#include <bits/stdc++.h>
using namespace std;

const double PI = acos(1);


struct Point
{
    int x,y;
    Point(){}
    Point(int x1, int y1)
    {
        x = x1;
        y = y1;
    }
};
struct Vector
{   Point a;
    Vector (Point c, Point b)
    {
        a.x = b.x - c.x;
        a.y = b.y - c.y;;
    }
    double len()
    {
         return sqrt(a.x * a.x + a.y * a.y);
    }
};

Vector operator+(Vector a, Vector b)
{
    return Vector(Point(0, 0), Point(a.a.x + b.a.x, a.a.y + b.a.y));
}
double operator^(Vector a, Vector b)
{
    return a.a.x * b.a.y - b.a.x * a.a.y;
}
double operator*(Vector a, Vector b)
{
    return a.a.x * b.a.x + a.a.y * b.a.y;
}
int main()
{

}
