Initial commit of UserAdmin reference implementation

The implementation stores the UserAdmin database in a property file
pointed to by the system property org.osgi.impl.service.useradmin.db . 
The format is readable by humans (it is a property file), but the
semantics is a bit complicated. I will describe the format shortly.

Jan Sparud
Gatespace
