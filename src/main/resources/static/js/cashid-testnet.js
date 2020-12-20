(function(cashid) {

	window.addEventListener("load", function() {
		let csrfElement = document.getElementById("csrf");
		let errorElement = document.getElementById("error");
		let nonce;

		cashid.initForm = function() {
			let authData = new FormData();
			authData.append(csrfElement.name, csrfElement.value);
			return authData;
		}

		document.getElementById("get-request").addEventListener("click", function() {
			errorElement.innerText = "";
			let address = document.getElementById("address").value;
			let authData = cashid.initForm();
			authData.append("address", address);
			let xhr = new XMLHttpRequest();
			xhr.open("POST", "/requestnonce");
			xhr.onload = function(e) {
				if (xhr.readyState === 4 && xhr.status === 200) {
					nonce = xhr.responseText;
					document.getElementById("request").value = "cashid:" + window.location.host + "/cashid/?x=" + nonce;
				} else {
					errorElement.innerText = "Error whilst requesting nonce. readyState = " + xhr.readyState + 
						", status = " + xhr.status +  
						", statusText = " + xhr.statusText + 
						", error: " + xhr.responseText;
				}
			};
			xhr.onerror = function (e) {
				console.log("Error whilst requesting nonce: " + e.error);
			}
			xhr.send(authData);
		});

		document.getElementById("login").addEventListener("click", function() {
			let address = document.getElementById("address").value;
			let json = JSON.stringify({
				'request': document.getElementById("request").value,
				'address': address,
				'signature': document.getElementById("signature").value
			});
			let xhr = new XMLHttpRequest();
			xhr.open("POST", "/cashid");
			xhr.setRequestHeader("Content-Type", "application/json");
			xhr.onload = function(e) {
				if (xhr.readyState === 4 && xhr.status === 200) {
					let authData = cashid.initForm();
					authData.append("nonce", nonce);
					authData.append("address", address);
					let xhrb = new XMLHttpRequest();
					xhrb.open("POST", "/cashidauth");
					xhrb.onload = function(e) {
						if (xhrb.readyState === 4 && xhrb.status === 200) {
							window.location.reload();
						}
					}
					xhrb.send(authData);
				}
			};
			xhr.onerror = function() {
				console.error(xhr.statusText);
			};
			xhr.send(json);
		});

	});
}(window.cashid = window.cashid || {}));
