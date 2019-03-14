;
if(typeof(window.rest) === 'undefined' || window.rest == null){
    window.rest = {};
}

rest.post = function(url, data){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'POST',
            url: url,
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(data),
            success: function(data, status, jqXHR){
                var l = jqXHR.getResponseHeader('Location');
                if(l){
                    jQuery.ajax({
                        method: 'GET',
                        url: l,
                        success: function(data, status, jqXHR){
                            resolve(data, status, jqXHR);
                        },
                        error: function(jqXHR, status, error){
                            reject(jqXHR, status, error);
                        }
                    })
                }else{
                    resolve(data, status, jqXHR);
                }
            },
            error: function(jqXHR, status, error){
                reject(jqXHR, status, error);
            }
        });
    });

    return p;
}

rest.httpDelete = function(url){
    var p = new Promise(function(resolve, reject){
        jQuery.ajax({
            method: 'DELETE',
            url: url,
            success: function(data, status, jqXHR){
                resolve(data, status, jqXHR);
            },
            error: function(jqXHR, status, error){
                reject(jqXHR, status, error);
            }
        });
    });

    return p;
}