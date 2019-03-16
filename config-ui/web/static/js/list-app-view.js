

class ListAppView extends React.Component {

    state = {
        apps: []
    };

    props = {};

    constructor(props) {
        super(props);
        this.props = props;
        this.state.apps = props.apps.slice(0);
        utils.sortField(this.state.apps, 'name');
    }


    remove(app){
        var c;
        c = "Are you sure you want to ";
        c += "<span style='font-weight: bold; color: red'>delete</span> ";
        c += "the app <span style='font-weight: bold;'>";
        c += app.name;
        c += "</span>.";

        var self = this;
        showConfirmDialog({content: c, title: 'Delete App'}).then(()=>{
            rest.httpDelete('/rest/apps/' + app.name).then(()=>{
                var arr = utils.findAndDelete(self.state.apps, 'name', app.name);
                self.setState({apps: arr});
            });
        });
    }

    render() {
        var self = this;
        const RenderRow = function (app) {
            return (
                <tr key={app._id.$oid}>
                    <td style={{padding: '0px'}}>
                        <div style={{flexDirection: 'row', display: 'flex',  justifyContent: 'center'}}>
                            <i  className="silk silk-pencil" style={{cursor: 'pointer', marginRight: '10px'}}></i>
                            <i onClick={()=>{self.remove(app)}} className="silk silk-cross" style={{cursor: 'pointer'}}></i>
                        </div>
                    </td>
                    <td><a href="#">{app.name}</a></td>
                    <td>{app.label}</td>
                    <td>{moment(new Date(app.lastUpdate)).fromNow()}</td>
                    <td style={{textAlign: 'right'}}>{utils.formatDate(app.creationDate)}</td>
                </tr>
            );
        };

        return (
            <div>
                <div style={{ display: 'flex', justifyContent: 'flex-start' }}>
                    <a href="#" data-toggle="modal" data-target={'#'+ this.props.createModal}>
                        <i className="fas fa-plus-circle"></i>
                        Create
                    </a>
                </div>
                <table className="table table-striped table-bordered table-hover table-condensed">
                    <thead>
                        <tr>
                            <td style={{width: '60px'}}></td>
                            <td>Name</td>
                            <td>Label</td>
                            <td>Last Update</td>
                            <td>Creation Date</td>
                        </tr>
                    </thead>
                    <tbody>
                        {this.state.apps.map(RenderRow)}
                    </tbody>
                </table>
            </div>
        );
    }
}