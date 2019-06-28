class Nav extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        var match = this.props.match;

        if(match == null){
            return <div></div>
        }

        var ten = match.params.ten;
        var realm = match.params.realm;

        const hasTenant = utils.notEmpty(ten);
        const hasRealm = utils.notEmpty(realm);

        const HomeLink = function(){
            return <ReactRouterDOM.Link to={'/'}>Home</ReactRouterDOM.Link>;
        }

        if(hasTenant && hasRealm){
            const TenantLink = function(){
                return <ReactRouterDOM.Link to={'/tenants/' + ten +'/realms/'}>{ten}</ReactRouterDOM.Link>;
            }
            return <div style={{fontSize: '22px'}}><HomeLink/> &raquo; <AppLink/> &raquo; <span style={{fontWeight: 'bold'}}>{realm}</span></div>
        }

        if(hasTenant){
            return <div style={{fontSize: '22px'}}><HomeLink/> &raquo;  <span style={{fontWeight: 'bold'}}>{ten}</span></div>
        }

        return <div style={{fontSize: '22px'}}><span style={{fontWeight: 'bold'}}>Home</span></div>;
    }
}