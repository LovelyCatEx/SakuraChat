import {Route, Routes} from 'react-router-dom'
import './App.css'
import {AuthorizationPage} from "./pages/auth/AuthorizationPage.tsx";
import {MainContainer} from "./pages/main/MainContainer.tsx";
import {InitializerPage} from "./pages/InitializerPage.tsx";
import {RequireAuthComponent} from "./components/auth/RequireAuthComponent.tsx";
import MainPage from "./pages/MainPage.tsx";
import TermsOfServicePage from "./pages/TermsOfServicePage.tsx";
import PrivacyPolicyPage from "./pages/PrivacyPolicyPage.tsx";
import {ConfigProvider} from "antd";

function App() {
  return (
      <ConfigProvider
          theme={{
              token: {
                  colorPrimary: '#FF8DA1',
                  borderRadius: 12,
                  fontFamily: 'Inter, system-ui, sans-serif',
              },
              components: {
                  Layout: {
                      headerBg: 'rgba(255, 255, 255, 0.7)',
                      siderBg: '#ffffff',
                  },
                  Menu: {
                      itemBorderRadius: 12,
                      itemSelectedBg: 'rgba(255,240,243,0.8)',
                      itemSelectedColor: '#FF8DA1',
                  },
              },
          }}
      >
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
      </ConfigProvider>
  )
}

export default App
