
;
if(typeof(window.validator) === 'undefined' || window.validator == null){
    window.validator = {};
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