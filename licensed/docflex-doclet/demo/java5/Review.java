package java5;

import java.lang.annotation.*;

@Documented
public @interface Review
{
  public static enum Grade { EXCELLENT, SATISFACTORY, UNSATISFACTORY };

  Grade  grade();
  String reviewer();
  String comment() default "";
}
