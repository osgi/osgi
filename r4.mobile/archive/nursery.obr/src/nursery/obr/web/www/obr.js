 	   function messageById(id) {
 	   	 var message = document.getElementById("message");
 	   		var node = document.getElementById("tt"+id);
 	   		message.innerHTML = node.innerHTML;
 	   }
 	   
 	   function clear() {
 	   	 alert(" clear");
 	   	 var message = document.getElementById("message");
		 message.innerHTML = " ";	
	   }   	