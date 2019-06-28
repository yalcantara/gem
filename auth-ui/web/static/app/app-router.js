class AppRouter extends React.Component{

    state = {
        tenants: [],
        crtTenant: {
            $realms: []
        },

        realms: [],
        users: []
    };

    rootPathCalled = false;

    constructor(props){
        super(props);
    }

    handleRealmPath(match){
        var self = this;
        if(match.params.tenant && match.params.realm){

            var tenantName = match.params.tenant;
            var realmName = match.params.realm;
            var realm = utils.find(this.state.realms, 'name', realmName);
            if(realm){

                self.state.crtRealm = realm;
                this.setState({crtRealm: realm});
                if(utils.isEmpty(realm.$keys)){
                    rest.get('/rest/auth/tenants/by-name/' + tenantName + '/realms/by-name/' + realmName +'/users').then(function(res){
                        var list = res.data;
                        utils.sortField(list, 'name');
                        realm.$keys = list;

                        self.state.keys = list;
                        self.setState({keys: list});
                    });
                }else{
                    var list = realm.$keys;
                    self.state.keys = list;
                    self.setState({keys: list});
                }
            }
        }
    }

    handleTenantPath(match){
        var self = this;
          
        if(match.params.tenant){
            var tenantName = match.params.tenant;
            var tenant = utils.find(this.state.tenants, 'name', tenantName);
            if(tenant){
                this.state.crtTenant = tenant;
                this.setState({crtApp: tenant});
                if(utils.isEmpty(tenant.$realms)){
                    rest.get('/rest/auth/tenants/by-name/' + tenantName + '/realms').then(function(res){
                        var list = res.data;
                        utils.sortField(list, 'name');
                        tenant.$realms = list;
                        //setting empty $keys field.
                        list.forEach((e)=>e.$keys=[]);

                        self.state.realms = list;
                        self.setState({realms: list});

                        self.handleRealmPath(match);
                    });
                }else{
                    var list = tenant.$realms;
                    self.state.realms = list;
                    self.setState({realms: list});
                    self.handleRealmPath(match);
                }
            }
        }
    }

    handleRootPath(match){
        //this path should always be called.
        var self = this;
        if(utils.isEmpty(this.state.tenants)){
            rest.get('/rest/auth/tenants').then(function(res){
                var list = res.data;
                utils.sortField(list, 'name');
                self.state.tenants = list;
                self.setState({tenants: list});
                self.handleTenantPath(match);
            });
        }else{
            self.handleTenantPath(match);
        }
    }

    handlePath(match){
        if(utils.isEmpty(match) || utils.isEmpty(match.params) ){
            return;
        }
        
        this.handleRootPath(match);
    }

 

    
    // ----- Tenants -----
    createTenantHandler(record){
        var list = this.state.tenants;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({tenants: list});
    }

    deleteTenantHandler(record){
        var list = this.state.tenants;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({tenants: list});
    }

    updateTenantHandler(record){
        var prev = utils.findAndReplaceOne(this.state.tenants, 'id', record);
        record.$realms = prev.$realms;
        this.setState({tenants: this.state.tenants});
    }
    // ---------------

    
    // ----- Realms -----
    createRealmHandler(record){
        var list = this.state.crtTenant.$realms;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({realms: list});
    }

    deleteRealmHandler(record){
        var list = this.state.crtTenant.$realms;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({realms: list});
    }

    updateRealmHandler(record){
        var list = this.state.crtTenant.$realms;
        var prev = utils.findAndReplaceOne(list, '_id', record);
        record.$keys = prev.$keys;
        this.setState({realms: list});
    }
    // ----------------

    // ----- Keys -----
    createKeyHandler(record){
        var list = this.state.crtRealm.$keys;
        list.push(record);
        utils.sortField(list, 'name');
        this.setState({keys: list});
    }

    deleteKeyHandler(record){
        var list = this.state.crtRealm.$keys;
        utils.findAndDelete(list, 'name', record.name);
        this.setState({keys: list});
    }

    updateKeyHandler(record){
        var list = this.state.crtRealm.$keys;
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
                    <ReactRouterDOM.Route exact path="/:sub(tenants|)" ref="tenantList"
                        render={(props)=>(
                            <ListTenantView {...props} 
                                list={this.state.tenants} 

                                createHandler={(record)=>this.createTenantHandler(record)}
                                deleteHandler={(record)=>this.deleteTenantHandler(record)}
                                updateHandler={(record)=>this.updateTenantHandler(record)}
                                
                                mountHandler={(match)=>this.mountHandler(match)}/>
                        )}/>


                    <ReactRouterDOM.Route exact path="/tenants/:tenant/realms" ref="realmList"
                        render={(props)=>(
                            <ListRealmView {...props} 
                                list={this.state.realms} 
                                crtTenant={this.state.crtTenant}

                                createHandler={(record)=>this.createRealmHandler(record)}
                                deleteHandler={(record)=>this.deleteRealmHandler(record)}
                                updateHandler={(record)=>this.updateRealmHandler(record)}

                                mountHandler={(match)=>this.mountHandler(match)} />
                        )}/>

                    <ReactRouterDOM.Route exact path="/tenants/:tenant/realms/:realm/users" ref="userList"
                        render={(props)=>(
                            <ListKeyView {...props} 
                                list={this.state.keys} 
                                crtApp={this.state.crtApp} 
                                crtRealm={this.state.crtRealm}

                                createHandler={(record)=>this.createKeyHandler(record)}
                                deleteHandler={(record)=>this.deleteKeyHandler(record)}
                                updateHandler={(record)=>this.updateKeyHandler(record)}

                                mountHandler={(match)=>this.mountHandler(match)} />
                        )}/>
            </ReactRouterDOM.HashRouter>
        );
    }
}