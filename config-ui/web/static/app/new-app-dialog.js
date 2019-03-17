class NewAppDialog extends React.Component { 
            
    props = {};

    form = null;
    errorHolder = null;
    
    constructor(props) { 
        super(props); 
        this.state = {}; 
        this.state.name = ''; 

        this.props = props;

        this.html = {};
        this.html.modal = React.createRef();
        this.html.form = React.createRef();
        this.html.name = React.createRef();
        this.html.msg = React.createRef();
        this.html.label = React.createRef();
        this.html.errorHolder = React.createRef();

        this.save = this.save.bind(this);

    } 

    beforeShow(){
        var msg = this.html.msg.current;
        var nameInput = this.html.name.current

        nameInput.value = '';
        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';
        this.errorHolder.css('display', 'none');
    }

    validate(){
        
        var msg = this.html.msg.current;
        var nameInput = this.html.name.current;
        var name = this.html.name.current.value;

        if(name == null || name == '' || name.trim() == ''){
            nameInput.classList.add('is-invalid');
            msg.className = 'invalid-feedback';
            msg.style.display = 'block';
            msg.innerHTML = 'The app name is required.';
            nameInput.focus();
            
            return false;
        }

        nameInput.classList.remove('is-invalid');
        msg.style.display = 'none';

        return true;
    }
    
    save(event) {
        
        if(this.validate() == false){
            return;
        }

        var name = this.html.name.current.value;
        var label = this.html.label.current.value;
        
        var app = {name: name, label: label};
        var self = this;
        rest.postAndGet('/rest/config/apps', app).then(function(data, status, jqXHR){
            self.errorHolder.css('display', 'none');
            if(self.okCallback){
                self.okCallback(data, status, jqXHR);
            }
        }).catch(function(jqXHR, status, error){
            var msg = 'There was an error in the system. Please try again later.';
            if(jqXHR.status === 409 || jqXHR.status == 400){
                msg = jqXHR.responseText;
            }
            self.errorHolder.css('display', 'block');
            self.errorHolder.html(msg);
        });
    } 

    handleKeyPress(event){
        var key = event.key;
        var which = event.which;
        if(key == 'Enter' || which == 13){
            this.save();
        }
    }
    
    componentDidMount() {
        this.form = jQuery(this.html.form.current);
        this.errorHolder = jQuery(this.html.errorHolder.current);

        const self = this;
        var nameInput = jQuery(this.html.name.current);

        this.form.find('input, button').on('keypress', (event)=>{self.handleKeyPress(event)});

        jQuery(this.html.modal.current).on('shown.bs.modal', function(){
            nameInput.focus();
        });

        jQuery(this.html.modal.current).on('show.bs.modal', function(){
            self.beforeShow();
        });
    }
    
    render() { 
        return (
            <div id={this.props.id} ref={this.html.modal} className="modal fade"
                tabIndex="-1" role="dialog" aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered modal-sm" role="document">
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
                                        Name: * <input ref={this.html.name} className="form-control" type="text"/>
                                    </label>
                                    <div ref={this.html.msg} style={{display: 'none'}}></div>
                                </div>
                                <div className="form-group">
                                    <label style={{width: '100%'}}>
                                        Label:  <input ref={this.html.label} className="form-control" type="text"/>
                                    </label>
                                </div>
                                <div ref={this.html.errorHolder} className="alert alert-danger" style={{display: 'none'}}></div>
                                <div className="form-group" style={{display: 'flex', justifyContent: 'flex-end'}}>
                                    <button type="button" className="btn btn-secondary" data-dismiss="modal" style={{marginRight: '5px'}}>Close</button>
                                    <button onClick={this.save} type="button" className="btn btn-success">Save</button>
                                </div>
                            </form> 
                        </div>
                    </div>
                </div>
            </div>
        ); 
    } 
} 