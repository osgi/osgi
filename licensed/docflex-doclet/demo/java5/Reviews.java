package java5;

import java.lang.annotation.*;

@Documented
public @interface Reviews
{
  Review[] value();
}
