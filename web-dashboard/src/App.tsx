import {Route, Routes} from 'react-router-dom'
import './App.css'
import {AuthorizationPage} from "./pages/auth/AuthorizationPage.tsx";
import {MainContainer} from "./pages/main/MainContainer.tsx";
import {InitializerPage} from "./pages/InitializerPage.tsx";
import {RequireAuthComponent} from "./components/auth/RequireAuthComponent.tsx";
import MainPage from "./pages/MainPage.tsx";
import TermsOfServicePage from "./pages/TermsOfServicePage.tsx";
import PrivacyPolicyPage from "./pages/PrivacyPolicyPage.tsx";

function App() {
  return (
      <div>
          <Routes>
              <Route path="/" element={<MainPage />} />
              <Route path="/terms" element={<TermsOfServicePage />} />
              <Route path="/privacy" element={<PrivacyPolicyPage />} />
              <Route path="/initializer" element={<InitializerPage />} />
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
