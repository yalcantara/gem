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
                if(other[field] == val){
                    return other;
                }
            }
        }
    }

    return null;
};

utils.findAndReplace = function(arr, field, val){
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){
                if(other[field] === val[field]){
                    arr[i] = val;
                }
            }
        }
    }
};

utils.findAndDelete = function(arr, field, val){
    var ans = [];
    if(arr && arr.length > 0){
        for(var i =0; i < arr.length; i++){
            var other = arr[i];
            if(other){
                if(other[field] === val){
                    continue;
                }
                ans.push(other);
            }else{
                ans.push(other);
            }
        }
    }

    return ans;
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