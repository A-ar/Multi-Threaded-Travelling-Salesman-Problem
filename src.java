import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class points
{
    String name;
    int x_c;
    int y_c;
    points(int x){
        thread_flag=new boolean[x];
    }
    boolean[] thread_flag; //these values make the threads operate on all points without interrupting each other
}
// all the static members are common for all thread objects.
class TSP extends Thread{
    TSP(int x){
        this.pass_flag=x;
    }
    static void init(int x){
        min=new double[x];
        answer=new ArrayList[x];
    }
    static int tn;  //total number of threads
    int pass_flag; //this variable decides the section of the total points that the thread object will be acting on
    static ArrayList<points> p=new ArrayList<>();  //contains all the points
    static points sp;         //starting point
    static int isp,n;         //isp is the index of the starting point n is total points
    static double[] min;//just a minimum value for each section.
    static ArrayList<Double>[] answer;   //answer stored here. the one is chosen with the smallest cost
    static ConcurrentHashMap<String,ArrayList<Double>> map= new ConcurrentHashMap<>(); //this is the only object which is shared by all three threads.
    //The run methods works like this:
    //depending on the value of pass flag(0,1....tn) thread 1 will run a loop for points 0 to n/tn.
    // thread 2 will run a loop for points n/tn to 2n/tn.
    // thread 3 will run a loop for points 2n/tn to 3n/tn.
    // thread tn will run a loop for points (tn-1)*n/tn to n/
    // The answer with minimum cost will be stored in the answer arraylist vector.
    public void run(){
        for (int g = (n / tn)*pass_flag; g < (n*(pass_flag+1)) / tn; g++) {
            if (g != isp) {
                ArrayList<Double> temp = cost(n, g, sp, n, pass_flag);
                double var = temp.get(0) + dist(p.get(g), sp);
                if (var < min[pass_flag]) {
                    min[pass_flag] = var;
                    answer[pass_flag]= temp;
                }
            }
        }
    }


    double dist(points x,points y)
    {
        double sumsq = Math.pow(x.x_c-y.x_c,2)+Math.pow(x.y_c-y.y_c,2);
        return Math.sqrt(sumsq);
    }

    ArrayList<Double> cost(int S, int ini, points sp, int n,int t_flag)   //ini is the input index, S is the size
    {
        //keys for the table are essentially size input index and set of points
        String dp=String.valueOf(S)+"#"+String.valueOf(ini);
        for(int i=0;i<n;i++){
            dp=dp+"#"+p.get(i).thread_flag[t_flag];

        }
        if(map.containsKey(dp)) return new ArrayList<>(map.get(dp));
        ArrayList<Double> ret=new ArrayList<>();
        ArrayList<Double> tlist=new ArrayList<>();
        double min=Double.MAX_VALUE;
        if(S==2)
        {
            ret.add(dist(sp,p.get(ini)));
            ret.add((double)ini);
            return ret;
        }
        p.get(ini).thread_flag[t_flag]=false;
        for(int ik=0;ik<n;ik++)
        {
            if(p.get(ik).thread_flag[t_flag])
            {
                ret=cost(S-1,ik,sp,n, t_flag);
                double var=ret.get(0)+dist(p.get(ik),p.get(ini));
                if(var<min)
                {
                    tlist=ret;
                    min=var;
                }
            }
        }
        p.get(ini).thread_flag[t_flag]=true;
        tlist.set(0,min);
        tlist.add((double)ini);
        map.put(dp,new ArrayList<>(tlist));
        return tlist;
    }
}
class Main{
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.println("Enter number of threads to run in parallel:");
        int tn=in.nextInt();
        TSP[] t_arr=new TSP[tn];
        for(int i=0;i<tn;i++){
            t_arr[i]=new TSP(i);
        }
        TSP.tn=tn;
        TSP.init(tn);
        for(int i=0;i<tn;i++){
            TSP.min[i]=Integer.MAX_VALUE;
        }
        System.out.print("enter the number of cities/points:");
        int n=in.nextInt();
        TSP.n=n;
        for(int i=0;i<n;i++)
        {
            System.out.println("enter name and the co-ordinates for the point("+(i+1)+")");
            points temp=new points(tn);
            temp.name=in.next();
            temp.x_c=in.nextInt();
            temp.y_c=in.nextInt();
            for(int j=0;j<tn;j++)
                temp.thread_flag[j]=true;
            TSP.p.add(temp);
        }
        System.out.println("enter starting point:");
        String cpn=in.next();
        for(int i=0;i<n;i++)
        {
            if(cpn.compareTo(TSP.p.get(i).name)==0)
            {
                TSP.sp=TSP.p.get(i);
                TSP.isp=i;
                for(int j=0;j<tn;j++)
                    TSP.p.get(i).thread_flag[j]=false;
                break;
            }
        }

        for(int i=0;i<tn;i++) t_arr[i].start();
        try {
            for(int i=0;i<tn;i++) t_arr[i].join();
        }catch (Exception e){System.out.println(e);}

        double min_f=Integer.MAX_VALUE;
        int ans_index=0;
        for(int i=0;i<tn;i++){
            if(TSP.min[i]<min_f){
                min_f=TSP.min[i];
                ans_index=i;
            }
        }
        System.out.print("the optimal path is: "+TSP.p.get(TSP.isp).name+"-->");
        for(int i=1;i<=n-1;i++){
            System.out.print(TSP.p.get(TSP.answer[ans_index].get(i).intValue()).name+"-->");
        }
        System.out.print(TSP.p.get(TSP.isp).name+"\n");
        System.out.println("The total distance covered in the trip is: "+(min_f));
    }
}
