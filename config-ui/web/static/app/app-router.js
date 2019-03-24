class AppRouter extends React.Component{

    state = {
        apps: [],
        crtApp: {
            $properties: []
        }
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


    propsMountHandler(match){
        var appName = match.params.app;
        var app = utils.find(this.state.apps, 'name', appName);
        var self = this;
        if(utils.isEmpty(app.$properties)){
            rest.get('/rest/config/apps/' + appName + '/properties').then(function(res){
                app.$properties = res.data;
                self.setState({crtApp: app});
            });
        }
    }

    render(){
        return (
            <ReactRouterDOM.HashRouter ref="router">
                    <ReactRouterDOM.Route exact path="/:sub(apps|)" ref="appList"
                        render={(props)=>(
                            <ListAppView {...props} list={this.state.apps} 
                                selected={this.state.selectedApp}
                                createHandler={(record)=>this.createAppHandler(record)}
                                deleteHandler={(record)=>this.deleteAppHandler(record)}
                                updateHandler={(record)=>this.updateAppHandler(record)}/>
                        )}/>


                    <ReactRouterDOM.Route path="/apps/:app/properties" ref="propList"
                        render={(props)=>(
                            <ListPropView {...props}
                            crtApp={this.state.crtApp}
                            mountHandler={(match)=>this.propsMountHandler(match)} />
                        )}/>
            </ReactRouterDOM.HashRouter>
        );
    }
}