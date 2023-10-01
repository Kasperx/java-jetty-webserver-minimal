
<<<<<<< HEAD
const url = "localhost:4000/";
=======
>>>>>>> 0e1a6606c9ee65a459ec3299e25c6fad69ac718e
function login()
{
    //Get the modal
    var modal = document.getElementById('login');
    var modal_login = document.getElementById('login_btn');
    const user = document.getElementById("uname").value;
    const pw = document.getElementById("psw").value;
    const remember = document.getElementById("remember").value;
    modal.style.display = "none";
    if(user != "" && pw != "" )
    {
<<<<<<< HEAD
		// const data = {'get':'admin', 'user':user, 'pw':pw, 'remember':remember};
		const data = {'get':'admin'};
        var params = '?'+encodeQueryData(data);
        // params = encodeQueryData(data);
=======
		
		var url = "localhost:4000/?";
		const data = {'get':'admin', 'user':user, 'pw':pw, 'remember':remember};
        var params = '?'+encodeQueryData(data);
        params = encodeQueryData(data);
>>>>>>> 0e1a6606c9ee65a459ec3299e25c6fad69ac718e
		/*
		var http = new XMLHttpRequest();
		http.open("GET", url+params, true);
		http.onreadystatechange = function()
		{
		    if(http.readyState == 4 && http.status == 200) {
		        alert(http.responseText);
		    }
		}
		http.send(null);
		*/
		//var params = "somevariable=somevalue&anothervariable=anothervalue";
<<<<<<< HEAD
		// const fullurl = url+params;
		const fullurl = params;
		console.log(fullurl);
		let http = new XMLHttpRequest();
		// http.open('POST', fullurl, true);
		http.open('GET', params);
		// http.send("/get=example");
		http.responseType = 'json';
		http.send(params);
		http.onreadystatechange = function(responseText)
		{
			if(this.readyState == 0 || this.readyState == 4 && this.status == 200)
			{
				alert();
		    }
		}
		http = null;
    }
}

function encodeQueryData(data)
{
	const ret = [];
    for (let d in data)
    {
		ret.push(encodeURIComponent(d) + '=' + encodeURIComponent(data[d]));
    }
    return ret.join('&');
}

function showWeather()
{
	// alert("weather");
	let http = new XMLHttpRequest();
	// http.open('GET', url, true);
	const data = {'get':'weather'};
	var params = '?'+encodeQueryData(data);
	http.open('GET', params);
	// http.send("/get=example");
	http.responseType = 'json';
	http.send();
	http.onreadystatechange = function()
	{
		if(this.readyState == 0 || this.readyState == 4 && this.status == 200)
		{
			let response = this.response;
			console.log(response);
			let data = JSON.parse(response);
			console.log(data);
		}
	}
	http = null;
}
=======
		const fullurl = url+params;
		console.log(fullurl);
		var http = new XMLHttpRequest();
		http.open('POST', fullurl, true);
		http.onreadystatechange = function()
		{
		    if(this.readyState == 4 && this.status == 200) {
		        alert(this.responseText);
		    }
		}
		http.send(params);
    }
}

function encodeQueryData(data) {
    const ret = [];
    for (let d in data)
    {
      ret.push(encodeURIComponent(d) + '=' + encodeURIComponent(data[d]));
    }
    return ret.join('&');
}
>>>>>>> 0e1a6606c9ee65a459ec3299e25c6fad69ac718e
