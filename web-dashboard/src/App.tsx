import {Route, Routes} from 'react-router-dom'
import './App.css'
import {AuthorizationPage} from "./pages/auth/AuthorizationPage.tsx";

function App() {
  return (
      <div>
          <Routes>
              <Route path="/auth/*" element={<AuthorizationPage />} />
          </Routes>
      </div>
  )
}

export default App
