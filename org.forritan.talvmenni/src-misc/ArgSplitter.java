import java.util.ArrayList;

public class ArgSplitter {
   private String[] splitArgs(String[] args, String[] regExps) {
      String[] result= args;
      for(String regExp : regExps) {
         result= this.splitArg(result, regExp);
      }
      return result;
   }

   private String[] splitArg(String[] args, String regExp) {
      ArrayList<String> result= new ArrayList<String>();
      for (String arg : args) {
         String[] s= arg.split(regExp);
         if (s[0].length() > 0) {
            result.add(s[0]);
         }
         for (int j= 1; j < s.length; j++) {
            if (s[j].length() > 0) {
               result.add(regExp.concat(s[j]));
            }
         }
      }
      return result.toArray(new String[] {});
   }
}
