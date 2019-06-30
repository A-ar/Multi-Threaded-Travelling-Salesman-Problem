
import java.util.*;

class TSP extends Thread{
    TSP(int x){
        this.pass_flag=x;
    }
    int pass_flag;
    static  points[] p=new points[10000];
    static points sp;
    static int isp,n;
    static double[] mi={Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE};
    static ArrayList<Double>[] pass=new ArrayList[3];
    static Hashtable<String, ArrayList<Double>> map=new Hashtable<>();
    public void run(){
        for (int g = (n / 3)*pass_flag; g < (n*(pass_flag+1)) / 3; g++) {
            if (g != isp) {
                ArrayList<Double> temp = cost(n, g, sp, n, pass_flag);
                double var = temp.get(0) + dist(p[g], sp);
                if (var < mi[pass_flag]) {
                    mi[pass_flag] = var;
                    pass[pass_flag]= temp;
                }
            }
        }
    }

    static class points
    {
        String name;
        int x_c;
        int y_c;
        boolean[] thread_flag=new boolean[3];
    }
    double dist(points x,points y)
    {
        double sumsq = Math.pow(x.x_c-y.x_c,2)+Math.pow(x.y_c-y.y_c,2);
        return Math.sqrt(sumsq);
    }

    ArrayList<Double> cost(int S, int ini, points sp, int n,int t_flag)   //ini is the input index, S is the size
    {
        String dp=String.valueOf(S)+"#"+String.valueOf(ini);
        for(int i=0;i<n;i++){
            dp=dp+"#"+p[i].thread_flag[t_flag];
        }
        if(map.containsKey(dp)) return map.get(dp);
        ArrayList<Double> ret=new ArrayList<>();
        ArrayList<Double> tlist=new ArrayList<>();
        double min=Double.MAX_VALUE;
        if(S==2)
        {
            ret.add(dist(sp,p[ini]));
            ret.add((double)ini);
            return ret;
        }
        p[ini].thread_flag[t_flag]=false;
        for(int ik=0;ik<n;ik++)
        {
            if(p[ik].thread_flag[t_flag])
            {
                ret=cost(S-1,ik,sp,n, t_flag);
                double var=ret.get(0)+dist(p[ik],p[ini]);
                if(var<min)
                {
                    tlist=ret;
                    min=var;
                }
            }
        }
        p[ini].thread_flag[t_flag]=true;
        tlist.set(0,min);
        tlist.add((double)ini);
        map.put(dp,tlist);
        return tlist;
    }
}
class Main{
    public static void main(String[] args){
        TSP t1=new TSP(0);
        TSP t2=new TSP(1);
        TSP t3=new TSP(2);
        Scanner in = new Scanner(System.in);
        System.out.print("enter the number of cities/points:");
        int n=in.nextInt();
        TSP.n=n;
        for(int i=0;i<n;i++)
        {
            System.out.println("enter name and the co-ordinates for the point("+(i+1)+")");
            TSP.points temp=new TSP.points();
            temp.name=in.next();
            temp.x_c=in.nextInt();
            temp.y_c=in.nextInt();
            temp.thread_flag[0]=true;
            temp.thread_flag[1]=true;
            temp.thread_flag[2]=true;
            TSP.p[i]=temp;
        }
        System.out.println("enter starting point:");
        String cpn=in.next();
        for(int i=0;i<n;i++)
        {
            if(cpn.compareTo(TSP.p[i].name)==0)
            {
                TSP.sp=TSP.p[i];
                TSP.isp=i;
                TSP.p[i].thread_flag[0]=false;
                TSP.p[i].thread_flag[1]=false;
                TSP.p[i].thread_flag[2]=false;
                break;
            }
        }


        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        }catch (Exception e){System.out.println(e);}

        double min_f=Integer.MAX_VALUE;
        int ans_index=0;
        for(int i=0;i<3;i++){
            if(TSP.mi[i]<min_f){
                min_f=TSP.mi[i];
                ans_index=i;
            }
        }
        System.out.print("the optimal path is: "+TSP.p[TSP.isp].name+"-->");
        for(int i=1;i<=n-1;i++){
            System.out.print(TSP.p[TSP.pass[ans_index].get(i).intValue()].name+"-->");
        }
        System.out.print(TSP.p[TSP.isp].name+"\n");
        System.out.println("The total distance covered in the trip is: "+(min_f));
    }
}
