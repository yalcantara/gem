;
if(typeof(window.utils) === 'undefined' || window.utils == null){
    window.utils = {};
}


utils.notEmpty = function(val){
    return !utils.isEmpty(val);
}


utils.isEmpty = function(val){
    if(typeof(val) == 'undefined' || val == null){
        return true;
    }

    if(jQuery.type( val ) === "string" && val == ''){
        return true;
    }

    if(jQuery.isArray(val)){
        if(val.length == 0){
            return true;
        }
    }

    return false;
}

utils.packEmail = function(arg){
    var str;
    if(arg instanceof jQuery){
        str = val();
    }else{
        str = arg;
    }

    var lower = str.toLowerCase();
    var ans = '';
    for(var i =0; i < str.length; i++){
        var c = lower[i];
        var o = str[i];

        if((c >= 'a' && c <= 'z') || c == '@' || c == '_' || c == '.' || c == ''){
            ans += o;
        }
    }

    return ans;
};

utils.find = function(arr, field, val){
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){

                if(jQuery.isArray(field)){
                    for(var j =0; j < fields.length; j++){
                        var f = field[i];

                        if(other[f] == val){
                            return other;
                        }
                    }
                }else if(other[field] == val){
                    return other;
                }
            }
        }
    }

    return null;
};

utils.search = function(arr, field, val){
    if(arr && arr.length > 0){
        var ans = [];
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){
                if(jQuery.isArray(field)){
                    for(var j =0; j < fields.length; j++){
                        var f = field[i];

                        if(other[f] == val){
                            ans.push(other);
                        }
                    }
                }else if(other[field] == val){
                    ans.push(other);
                }
            }
        }
        return ans;
    }

    return [];
};


utils.findAndReplaceOne = function(arr, field, val){
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){

                if(typeof(other[field]) === 'undefined'){
                    continue;
                }

                if( other[field] === val[field]){
                    var prev = arr[i];
                    arr[i] = val;
                    return prev;
                }
            }
        }
    }
};

utils.findAndDelete = function(arr, field, val){
   
    var removed = 0;
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){
                if(other[field] === val){
                    //since we are modifiying the array, we have to reduce the index 
                    //so it can be added back again and continue right at 
                    //where the element was deleted.
                    arr.splice(i, 1);
                    i--;
                }
            }
        }
    }
};



utils.formatDate = function(unixTime){
    if(unixTime){
        return moment(new Date(unixTime)).format('MMMM Do YYYY, h:mm:ss a');
    }
    return '';
};

utils.sortField = function(arr, field){
    if(arr){
        arr.sort((x, y) => {
            var a = x[field];
            var b = y[field];
            if (a > b) {
                return 1;
            }
            if (a < b) {
                return -1;

            } if (a === b) {
                return 0;
            }
        });
    }
};

utils.assigIdToAll = function(arr){
    if(utils.isEmpty(arr)){
        return;
    }

    for(var i =0; i < arr.length; i++){
        var obj = arr[i];
        if(obj){
            obj.__id = utils.uuidv4();
        }
    }
};

utils.uuidv4 = function() {
    return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
      (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
};