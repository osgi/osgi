
function navigate() {
    document.writeln( "<table cols=5 width=600 bgcolor='#000070' ><tr>");
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/index.html class=navigate>Home</a></td>" )
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/bundles.html class=navigate>Bundles</a></td>" )
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/nursery.html class=navigate>Nursery</a></td>" )
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/tutorial.html class=navigate>Tutorial</a></td>" )
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/framework.html class=navigate>Framework</a></td>" )
    document.writeln( "<td width='15%'><a href=http://www.osgi.org/members/Javareview/content/legal.html class=navigate>Legal</a></td>" )
    document.writeln( "</tr></table>");
}   
    
function start() {
    document.writeln( "<img SRC='sub_header.gif' ALT='OSGi' BORDER=0 height=88 width=600>" )
    navigate()
}

function finish() {
    document.writeln( "&nbsp;<br>" );
    navigate()
    document.writeln( "<br><p class=footer>All material on this page can be found on the OSGi site in the member section. The appropriate OSGi disclaimers and OSGi member contractual obligations apply. Feedback, suggestions and comments please <a href='mailto:Peter.kriens@aQute.se'>here</a>. These pages are maintained for the OSGi by ERICSSON" )
}


var i
var j


function href( url ) {
    if ( ! url )
        return url

    nurl = url.toLowerCase()
    if ( nurl.indexOf( "http:" ) == 0
    || nurl.indexOf("mailto:" ) == 0
    || nurl.indexOf( "https:" ) == 0 )
        return url;

    if ( url.indexOf( "@" ) > 0 )
        return "mailto:" + url

    return "http://" + url
}


function anchor( title, anchor ) {
    document.write( "<a href=#"+anchor +">" + title + "</a><br>" );
}

function list( l ) {
    var del = ""
    var result = ""
    
    if ( l )
    {
        for ( j in l )
        {
            result += del + l[j]
            del = ", "
        }
    }
    return result
}


function bundles(type) {
    document.writeln( "<table cellspacing=0 cellpadding=2 border=0  class=index cols=3>");
    document.writeln( "<tr><td width=200 class=index ><h4>All Bundles</h4></td><td width=200>")
    var half = Math.round(BUNDLES.length/2)-1;
    for ( i in BUNDLES )
    {
        var description

        if ( ! BUNDLES[i].name )
            BUNDLES[i].name = BUNDLES[i].file

        description = BUNDLES[i].name

        if ( ! description )
            description = BUNDLES[i].description

        anchor( description, BUNDLES[i].file )
        if ( i >= half ) {
            document.writeln( "</td><td width=200>")
            half = 1000000000
        }
    }
    document.writeln( "</td></tr>")
    var half = Math.round(CATEGORIES.length/2) - 1;
    document.writeln( "<tr><td class=index width=200><h4>Categories</h4></td><td width=200>")
    for ( category in CATEGORIES )
    {
        anchor( CATEGORIES[category].name, "CAT" + CATEGORIES[category].name )
        if ( category >= half ) {
            document.writeln( "</td><td width=200>")
            half = 1000000000
        }
    }
    document.writeln( "<br><br></td></tr>" );
    
    for ( category in CATEGORIES )
    {
        var cat = CATEGORIES[ category ];
        
        document.writeln( "<tr><td width=200 class=index><h4><a href='reference.html#" + cat.name + "'>"  + cat.name + "</a></h4></td><td width=200>")
        document.write( "<a name='CAT" + cat.name + "'></a>" )
        var names = cat.members;
        var files = cat.files;
        var half = Math.round(names.length/2) ;
        for ( i in  names )
        {   
            anchor( names[i], files[i] )
            if ( i >= half ) {
                document.writeln( "</td><td width=200>")
                half = 1000000000
            }
        }
        document.writeln( "</td></tr>")
    }
    document.writeln( "</table>")


    document.write( "<h2>Detailed bundle information</h2>" )
    for ( i in BUNDLES )
    {
        document.write( "<a name='"+ BUNDLES[i].file + "'></a>" )
        document.writeln( "<table cellspacing=0 cellpadding=2 border=0 class=list cols=2><tr><td width=600 colspan=2>")
        
        var version = "";
        if ( BUNDLES[i].version )
            version = " v" + BUNDLES[i].version;
            
        document.writeln( "<h3>" + BUNDLES[i].name + version  + "</h3><p>" );

        if ( BUNDLES[i].description )
            document.writeln( BUNDLES[i].description + "<br><br>" );
        
        document.writeln( "</td></tr>" );
        
        galley( "Download",  "<a href='" + type + "/" + BUNDLES[i].file + "'>" + BUNDLES[i].file + "</a>" );
        if ( BUNDLES[i].vendor )        
            galley( "Authored by", "<a href='" + href(BUNDLES[i].contact) + "'>" + BUNDLES[i].vendor + "</a>" )

        if ( BUNDLES[i].doc )
            galley( "Documentation", "<a href='" + href(BUNDLES[i].doc) + "'>" + BUNDLES[i].doc + "</a>" );
            
        if ( BUNDLES[i].copyright )
            galley( "Copyright", BUNDLES[i].copyright );
            
        if ( BUNDLES[i].imports )
            galley( "Imports", list( BUNDLES[i].imports) )
            
        if ( BUNDLES[i].exports )
            galley( "Exports", list( BUNDLES[i].exports ) )
            
        if ( BUNDLES[i].categories )
            galley( "Categories", list( BUNDLES[i].categories ) )
        

        if ( BUNDLES[i].size )
            galley( "Size", Math.round(BUNDLES[i].size/1000) + " K");

        document.writeln( "</table><br>" );
    }
}       

function galley( label, value ) {
    document.writeln( "<tr><td class=label width=300>" + label + "</td><td width=300>" + value + "</td></tr>" )
}



