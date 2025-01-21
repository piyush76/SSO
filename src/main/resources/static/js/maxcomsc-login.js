// Maxcomsc SSO Login Integration
function redirectToSsoLogin() {
    window.location.href = '/sso/maxcomsc/login';
}

// Add SSO login button to existing form
Ext.onReady(function() {
    var loginForm = Ext.getCmp('loginForm');
    if (loginForm) {
        loginForm.add({
            xtype: 'button',
            text: 'Login with SSO',
            handler: redirectToSsoLogin
        });
        loginForm.doLayout();
    }
});
