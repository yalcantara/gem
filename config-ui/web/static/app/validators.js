
;
if(typeof(window.validator) === 'undefined' || window.validator == null){
    window.validator = {};
}



validator.clear = function(input, msg){
    input.value = '';
    input.classList.remove('is-invalid');
    if(msg){
        msg.style.display = 'none';
    }
}

validator.required = function(input, msg, text){
    var val = input.value;

    if(val == null || val == '' || val.trim() == ''){
        input.classList.remove('is-valid');
        input.classList.add('is-invalid');
        msg.className = 'invalid-feedback';
        msg.style.display = 'block';
        msg.innerHTML = text;
        
        return false;
    }


    input.classList.remove('is-invalid');
    msg.style.display = 'none';
};