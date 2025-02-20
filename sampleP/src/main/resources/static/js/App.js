import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import FriendsPage from "./components/FriendsPage";
import { Link } from "react-router-dom";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/friends" element={<FriendsPage />} />
            </Routes>
        </Router>
    );
}

export default App;