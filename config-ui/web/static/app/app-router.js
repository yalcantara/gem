class AppRouter extends React.Component{

    state = {
        apps: [],
        crtApp: {
            $properties: []
        },

        props: [],
        keys: []
    };

    rootPathCalled = false;

    constructor(props){
        super(props);
    }

    handlePropPath(match){
        var self = this;
        if(match.params.app && match.params.prop){

            var appName = match.params.app;
            var propName = match.params.prop;
            var prop = utils.find(this.state.props, 'name', propName);
            if(prop){

                self.state.crtProp = prop;
                this.setState({crtProp: prop});
                if(utils.isEmpty(prop.$keys)){
                    rest.get('/rest/config/apps/' + appName + '/properties/' + propName +'/keys').then(function(res){
                        var list = res.data;
                        utils.sortField(list, 'name');
                        prop.$keys = list;

                        self.state.keys = list;
                        self.setState({keys: list});
                    });
                }else{
                    var list = prop.$keys;
                    self.state.keys = list;
                    self.setState({keys: list});
                }
            }
        }
    }

    handleAppPath(match){
        var self = this;
          
        if(match.params.app){
            var appName = match.params.app;
            var app = utils.find(this.state.apps, 'name', appName);
            if(app){
                this.state.crtApp = app;
                this.setState({crtApp: app});
                if(utils.isEmpty(app.$properties)){
                    rest.get('/rest/config/apps/' + appName + '/properties').then(function(res){
                        var list = res.data;
                        utils.sortField(list, 'name');
                        app.$properties = list;
                        //setting empty $keys field.
                        list.forEach((e)=>e.$keys=[]);

                        self.state.props = list;
                        self.setState({props: list});

                        self.handlePropPath(match);
                    });
                }else{
                    var list = app.$properties;
                    self.state.props = list;
                    self.setState({props: list});
                    self.handlePropPath(match);
                }
            }
        }
    }

    handleRootPath(match){
        //this path should always be called.

        var self = this;
        if(utils.isEmpty(this.state.apps)){
            rest.get('/rest/config/apps').then(function(res){
                var list = res.data;
                utils.sortField(list, 'name');
                //setting empty $properties field.
                list.forEach((e)=>e.$properties=[]);

                self.state.apps = list;
                self.setState({apps: list});


                self.handleAppPath(match);
            });
        }else{
            self.handleAppPath(match);
        }
    }

    handlePath(match){
        if(utils.isEmpty(match) || utils.isEmpty(match.params) ){
            return;
        }
        
        this.handleRootPath(match);
    }

 

    
    // ----- App -----
    createAppHandler(record){
        var list = this.state.apps;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({apps: list});
    }

    deleteAppHandler(record){
        var list = this.state.apps;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({apps: list});
    }

    updateAppHandler(record){
        var prev = utils.findAndReplaceOne(this.state.apps, '_id', record);
        record.$properties = prev.$properties;
        this.setState({apps: this.state.apps});
    }
    // ---------------

    
    // ----- Prop -----
    createPropHandler(record){
        var list = this.state.crtApp.$properties;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({props: list});
    }

    deletePropHandler(record){
        var list = this.state.crtApp.$properties;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({props: list});
    }

    updatePropHandler(record){
        var list = this.state.crtApp.$properties;
        var prev = utils.findAndReplaceOne(list, '_id', record);
        record.$keys = prev.$keys;
        this.setState({props: list});
    }
    // ----------------

    // ----- Keys -----
    createKeyHandler(record){
        var list = this.state.crtProp.$keys;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({keys: list});
    }

    deleteKeyHandler(record){
        var list = this.state.crtProp.$keys;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({keys: list});
    }

    updateKeyHandler(record){
        var list = this.state.crtProp.$keys;
        var prev = utils.findAndReplaceOne(list, '_id', record);
        record.$keys = prev.$keys;
        this.setState({keys: list});
    }
    // ----------------


    mountHandler(match){
        this.handlePath(match);
    }

    render(){
        return (
            <ReactRouterDOM.HashRouter ref="router">
                    <ReactRouterDOM.Route exact path="/:sub(apps|)" ref="appList"
                        render={(props)=>(
                            <ListAppView {...props} 
                                list={this.state.apps} 

                                createHandler={(record)=>this.createAppHandler(record)}
                                deleteHandler={(record)=>this.deleteAppHandler(record)}
                                updateHandler={(record)=>this.updateAppHandler(record)}
                                
                                mountHandler={(match)=>this.mountHandler(match)}/>
                        )}/>


                    <ReactRouterDOM.Route exact path="/apps/:app/properties" ref="propList"
                        render={(props)=>(
                            <ListPropView {...props} 
                                list={this.state.props} 
                                crtApp={this.state.crtApp}

                                createHandler={(record)=>this.createPropHandler(record)}
                                deleteHandler={(record)=>this.deletePropHandler(record)}
                                updateHandler={(record)=>this.updatePropHandler(record)}

                                mountHandler={(match)=>this.mountHandler(match)} />
                        )}/>

                    <ReactRouterDOM.Route exact path="/apps/:app/properties/:prop/keys" ref="keyList"
                        render={(props)=>(
                            <ListKeyView {...props} 
                                list={this.state.keys} 
                                crtApp={this.state.crtApp} 
                                crtProp={this.state.crtProp}

                                createHandler={(record)=>this.createKeyHandler(record)}
                                deleteHandler={(record)=>this.deleteKeyHandler(record)}
                                updateHandler={(record)=>this.updateKeyHandler(record)}

                                mountHandler={(match)=>this.mountHandler(match)} />
                        )}/>
            </ReactRouterDOM.HashRouter>
        );
    }
}