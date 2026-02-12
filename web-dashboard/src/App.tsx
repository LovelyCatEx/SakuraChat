import {Route, Routes} from 'react-router-dom'
import './App.css'
import {AuthorizationPage} from "./pages/auth/AuthorizationPage.tsx";
import {MainContainer} from "./pages/main/MainContainer.tsx";
import {RequireAuthComponent} from "./components/auth/RequireAuthComponent.tsx";

function App() {
  return (
      <div>
          <Routes>
              <Route
                  path="/*"
                  element={
                    <RequireAuthComponent>
                        <MainContainer />
                    </RequireAuthComponent>
                  }
              />
              <Route path="/auth/*" element={<AuthorizationPage />} />
          </Routes>
      </div>
  )
}

export default App
