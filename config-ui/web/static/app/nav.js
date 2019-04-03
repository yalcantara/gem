class Nav extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        var match = this.props.match;

        if(match == null){
            return <div></div>
        }

        var app = match.params.app;
        var prop = match.params.prop;

        const hasApp = utils.notEmpty(app);
        const hasProp = utils.notEmpty(prop);

        const HomeLink = function(){
            return <ReactRouterDOM.Link to={'/'}>Home</ReactRouterDOM.Link>;
        }

        if(hasApp && hasProp){
            const AppLink = function(){
                return <ReactRouterDOM.Link to={'/apps/' + app +'/properties/'}>{app}</ReactRouterDOM.Link>;
            }
            return <div style={{fontSize: '22px'}}><HomeLink/> &raquo; <AppLink/> &raquo; <span style={{fontWeight: 'bold'}}>{prop}</span></div>
        }

        if(hasApp){
            return <div style={{fontSize: '22px'}}><HomeLink/> &raquo;  <span style={{fontWeight: 'bold'}}>{app}</span></div>
        }

        return <div style={{fontSize: '22px'}}><span style={{fontWeight: 'bold'}}>Home</span></div>;
    }
}