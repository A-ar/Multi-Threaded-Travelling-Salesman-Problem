#include<stdio.h>
#include<math.h>
#include<vector>
using namespace std;
int isp, n;			//n is the total number of points
struct points
{
  char name;
  int x_c;
  int y_c;
  bool flag;
};
struct points sp, p[30];
float dist(struct points x, struct points y)
{
  int sumsq = pow (x.x_c - y.x_c, 2) + pow (x.y_c - y.y_c, 2);
  return sqrt (sumsq);
}

vector<float> cost(int S, int ini)		//ini is the input index and head is the head of the path
{
  float var;
  int head;
  vector<float> ret;
  float min_ = 99998;
  p[ini].flag = 0;
  if (S != 2)
    {
    #pragma omp parallel
    for (int ik = 0; ik <= n - 1; ik++)
	{
	  if (p[ik].flag == 1)
	    {
	      ret = cost (S - 1, ik);
	      var = ((ret[0]) + dist (p[ik], p[ini]));
	      if (var < min_)
		    {
		  head = ini;
		  min_ = var;
          #pragma omp flush
		    }
	    }
	}
    p[ini].flag = 1;
    ret[0] = min_;
    ret.push_back (head);
    return ret;
    #pragma omp flush
    }
  else if (S == 2)
    {
      p[ini].flag = 1;
      ret.push_back (0);
      ret[0] = dist (sp, p[ini]);
      ret.push_back (ini);
      return ret;
    }
}

int main ()
{
  printf ("enter the number of citites/points:");
  scanf ("%d", &n);
  for (int i = 0; i < n; i++)
    {
      printf ("enter name and the co-ordinates for the point(%d):\n", i + 1);
      scanf (" %c", &p[i].name);
      scanf ("%d", &p[i].x_c);
      scanf ("%d", &p[i].y_c);
      p[i].flag = 1;
    }
  printf ("enter starting point:");
  char cpn;
  struct points ne;
  scanf (" %c", &cpn);
  #pragma omp master
  #pragma omp parallel
  for (int i = 0; i < n; i++)
    {
      if (cpn == p[i].name)
	  {
	    sp = p[i];
	    isp = i;
	    p[isp].flag = 0;
	    break;
	  }
    }
  int k;
  float mi = 999999;
  vector<float> pass;
  vector<float> tenp2;
  #pragma omp parallel for
  for (int i = 0; i <= n - 1; i++)
    {
      if (i != isp)
	    {
	        tenp2 = (cost (n, i));
	        float tenp = tenp2[0];
	        if (tenp < mi)
	            {
	            mi = tenp;
	            k = i;
	            pass = tenp2;
                #pragma omp flush
	            }
	     }
    }
  printf ("the optimal path is: %c-->", p[isp].name);
  for (int i = 1; i <= n - 1; i++)
    {
      printf ("%c-->", p[(int) pass[i]].name);
    }
  printf ("%c", p[isp].name);
  printf ("The total distance covered in the trip is: %f\n\n", mi + dist (sp, p[k]));
  return 0;
}
