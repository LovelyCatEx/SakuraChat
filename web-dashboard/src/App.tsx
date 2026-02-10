import {Route, Routes} from 'react-router-dom'
import './App.css'
import {AuthorizationPage} from "./pages/auth/AuthorizationPage.tsx";
import {MainContainer} from "./pages/MainContainer.tsx";

function App() {
  return (
      <div>
          <Routes>
              <Route path="/*" element={<MainContainer />} />
              <Route path="/auth/*" element={<AuthorizationPage />} />
          </Routes>
      </div>
  )
}

export default App
