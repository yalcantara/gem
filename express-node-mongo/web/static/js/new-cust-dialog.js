class NewCustomerDialog extends React.Component { 
            

    
    constructor(props) { 
        super(props); 
        this.state = {}; 
        this.state.firstName = ''; 

        this.html = {};
        this.html.modal = React.createRef();
        this.html.form = React.createRef();
        this.html.firstName = React.createRef();
        this.html.msg = React.createRef();
        this.html.lastName = React.createRef();

        this.save = this.save.bind(this);

        this.successCallback = props.successCallback;

        this.jsx = (
            <div id={props.id} ref={this.html.modal} className="modal fade modal-sm" style={{margin: '0px auto'}} tabIndex="-1" role="dialog">
                <div className="modal-dialog modal-dialog-centered modal-sm " role="document">
                    <div className="modal-content">
                        <div className="modal-header" style={{height: '35px', paddingTop: '5px'}}>
                            <span className="modal-title">Create New App</span>
                            <button type="button" className="close" data-dismiss="modal" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <form ref={this.html.form}>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        First Name: * <input ref={this.html.firstName} className="form-control" type="text"/>
                                    </label>
                                    <div ref={this.html.msg} style={{display: 'none'}}></div>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Last Name:  <input ref={this.html.lastName} className="form-control" type="text"/>
                                    </label>
                                </div>
                            </form>
                            <div style={{display: 'flex', justifyContent: 'flex-end'}}>
                                <button type="button" className="btn btn-default" data-dismiss="modal" style={{marginRight: '5px'}}>Close</button>
                                <button onClick={this.save} type="button" className="btn btn-primary btn-success">Save</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    } 

    beforeShow(){
        var msg = this.html.msg.current;
        var nameInput = this.html.firstName.current

        nameInput.value = '';
        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';
    }

    validate(){
        var form = this.html.form.current;
        var msg = this.html.msg.current;
        var nameInput = this.html.firstName.current
        var firstName = this.html.firstName.current.value;

        if(firstName == null || firstName == '' || firstName.trim() == ''){
            nameInput.classList.add('is-invalid');
            msg.className = 'invalid-feedback';
            msg.style.display = 'block';
            msg.innerHTML = 'The first name is required.';
            setTimeout(function(){
                nameInput.focus();
            });
            
            return false;
        }

        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';

        return true;
    }
    
    save(event) {
        
        if(this.validate()){
            var firstName = this.html.firstName.current.value;
            var lastName = this.html.lastName.current.value;
            
            var cust = {'First Name': name, 'Last Name': lastName};
            var self = this;
            rest.post('/rest/customers', cust).then((data)=>{
                if(self.successCallback){
                    self.successCallback();
                }
            });
            
        }
    } 
    
    componentDidMount() {
        const self = this;
        jQuery(this.html.modal.current).on('shown.bs.modal', function(){
            self.html.firstName.current.focus();
        });

        jQuery(this.html.modal.current).on('show.bs.modal', function(){
            self.beforeShow();
        });
    }
    
    render() { 
        return (this.jsx); 
    } 
} 