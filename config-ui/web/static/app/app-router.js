class AppRouter extends React.Component{

    state = {
        apps: [],
        selectedApp: {}
    };

    constructor(props){
        super(props);

        var self = this;
        rest.get('/rest/config/apps').then(function(res){
            var list = res.data;
            utils.sortField(list, 'name');
            self.setState({apps: list});
        });
    }

 

    createAppHandler(record){
        this.state.apps.push(record);
        utils.sortField(this.state.apps, 'name');
        this.setState({apps: this.state.apps});
    }

    deleteAppHandler(record){
        var arr = utils.findAndDelete(this.state.apps, 'name', record.name);
        this.setState({apps: arr});
    }

    updateAppHandler(record){
        utils.findAndReplace(this.state.apps, 'id', record);
        this.setState({apps: this.state.apps});
    }

    render(){
        return (
            <ReactRouterDOM.HashRouter>
                    <ReactRouterDOM.Route exact path="/" ref="appList"
                        render={(props)=>(
                            <ListAppView {...props} list={this.state.apps} 
                                selected={this.state.selectedApp}
                                createHandler={(record)=>this.createAppHandler(record)}
                                deleteHandler={(record)=>this.deleteAppHandler(record)}
                                updateHandler={(record)=>this.updateAppHandler(record)}/>
                        )}/>
            </ReactRouterDOM.HashRouter>
        );
    }
}