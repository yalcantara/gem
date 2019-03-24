class ListPropertyView extends React.Component{

    constructor(props){
        super(props);
    }

    call(param) {
        console.log('called: ' +param);
    }

    render(){
        return (
            <h2 ref="pop">Pop</h2>
        );
    }
}