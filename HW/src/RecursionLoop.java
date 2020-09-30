public class RecursionLoop
{
   public static void main(String[] args)
   {
      int num = 10;
      int sum = 0;
      int count = 0;
      
      for(int i = num ; i > 0; i--)
      {
         sum += num - count;
         count++;
      }
      
      System.out.println(sum);
   }
}