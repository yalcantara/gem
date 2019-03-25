class AppRouter extends React.Component{

    state = {
        apps: [],
        crtApp: {
            $properties: []
        },

        match: null
    };

    constructor(props){
        super(props);

        var self = this;
        rest.get('/rest/config/apps').then(function(res){
            var list = res.data;
            utils.sortField(list, 'name');
            //setting empty $properties field.
            list.forEach((e)=>e.$properties=[]);
            self.setState({apps: list});
            self.handlePath();
        });
    }

    handlePath(){
        var match = this.state.match;
        if(utils.isEmpty(match) || utils.isEmpty(match.params) ){
            return;
        }
          
        if(match.params.app){
            var appName = match.params.app;
            var app = utils.find(this.state.apps, 'name', appName);
            var self = this;
            if(app){
                this.setState({crtApp: app});
                if(utils.isEmpty(app.$properties)){
                    rest.get('/rest/config/apps/' + appName + '/properties').then(function(res){
                        var list = res.data;
                        utils.sortField(list, 'name');
                        app.$properties = list;
                        //setting empty $keys field.
                        list.forEach((e)=>e.$keys=[]);
                        self.setState({crtApp: app});
                    });
                }
            }
        }
    }

 

    
    // ----- App -----
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
        var prev = utils.findAndReplaceOne(this.state.apps, '_id', record);
        record.$properties = prev.$properties;
        this.setState({apps: this.state.apps});
    }
    // ---------------

    
    // ----- Prop -----
    createPropHandler(record){
        var props = this.state.crtApp.$properties;
        props.push(record);
        utils.sortField(props, 'name');
        this.setState({crtApp: this.state.crtApp});
    }
    // ----------------



    propsMountHandler(match){
        this.state.match = match;
        this.handlePath();
    }

    render(){
        return (
            <ReactRouterDOM.HashRouter ref="router">
                    <ReactRouterDOM.Route exact path="/:sub(apps|)" ref="appList"
                        render={(props)=>(
                            <ListAppView {...props} list={this.state.apps} 

                                createHandler={(record)=>this.createAppHandler(record)}
                                deleteHandler={(record)=>this.deleteAppHandler(record)}
                                updateHandler={(record)=>this.updateAppHandler(record)}/>
                        )}/>


                    <ReactRouterDOM.Route path="/apps/:app/properties" ref="propList"
                        render={(props)=>(
                            <ListPropView {...props} crtApp={this.state.crtApp}

                                createHandler={(record)=>this.createPropHandler(record)}
                                deleteHandler={(record)=>this.deletePropHandler(record)}
                                updateHandler={(record)=>this.updatePropHandler(record)}

                                mountHandler={(match)=>this.propsMountHandler(match)} />
                        )}/>
            </ReactRouterDOM.HashRouter>
        );
    }
}